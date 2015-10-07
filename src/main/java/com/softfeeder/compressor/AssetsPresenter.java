/**
 * The MIT License
 *
 *  Copyright (c) 2015, Ehab Al-Hakawati (e.hakawati@softfeeder.com)
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *  THE SOFTWARE.
 */
package com.softfeeder.compressor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.io.IOUtils;
import org.mozilla.javascript.ErrorReporter;
import org.mozilla.javascript.EvaluatorException;

import com.yahoo.platform.yui.compressor.CssCompressor;
import com.yahoo.platform.yui.compressor.JavaScriptCompressor;

public class AssetsPresenter extends TagSupport {

	private static final long serialVersionUID = 5071731143056606938L;
	private static final Logger LOG = Logger.getLogger(AssetsPresenter.class.getName());

	// -- Reduce IO
	private static final Set<String> generatedFiles = new HashSet<String>();

	// -- default collection
	private String collection = "header";

	// -- css/js
	private FileType type;

	// -- default value
	private boolean debug = Constants.DEBUG_MOOD;

	/**
	 * 
	 * @param type
	 */
	public void setType(String type) {
		this.type = FileType.valueOf(type.toUpperCase());
	}

	/**
	 * 
	 * @param collection
	 */
	public void setCollection(String collection) {
		this.collection = collection.toUpperCase();
	}

	/**
	 * 
	 * @param debug
	 */
	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.jsp.tagext.TagSupport#doEndTag()
	 */
	@SuppressWarnings({ "unchecked" })
	@Override
	public int doEndTag() throws JspException {

		final Map<FileType, Map<String, List<String>>> resources = (Map<FileType, Map<String, List<String>>>) this.pageContext
				.getAttribute(Constants.TAG_LIB_MAP_VALUE_KEY, PageContext.REQUEST_SCOPE);

		if (resources != null) {
			generateFileType(resources.get(this.type), this.type);
		} else {
			LOG.info("there is't and asset collected");
		}

		return SKIP_BODY;
	}

	/**
	 * 
	 * @param locations
	 * @param type
	 */
	private void generateFileType(Map<String, List<String>> locations, FileType type) {
		if (locations != null && locations.containsKey(this.collection)) {
			generateFileTypeLocation(locations.get(this.collection), type);
		}
	}

	/**
	 * 
	 * @param files
	 * @param type
	 */
	private void generateFileTypeLocation(List<String> files, FileType type) {

		if (debug && files != null) {
			for (String fileName : files) {
				printLink(type, fileName);
			}

		} else if (files != null) {

			final String targetName = getTargetFileName(files);

			if (!generatedFiles.contains(targetName)) {
				checkWriteMinFile(files, type, targetName);
				generatedFiles.add(targetName);
			}

			printLink(type, targetName);

		}
	}

	/**
	 * 
	 * @param files
	 * @return target file name
	 */
	private String getTargetFileName(List<String> files) {
		final StringBuilder minFileName = new StringBuilder();
		minFileName.append(Constants.START_TIME);
		minFileName.append("|");
		for (String fileName : files) {
			minFileName.append(fileName);
			minFileName.append("|");
		}
		minFileName.append(this.collection);
		return String.format("%s.%s", getMD5Name(minFileName.toString()), this.type.toString());
	}

	/**
	 * 
	 * @param type
	 * @param targetName
	 */
	private void printLink(FileType type, String targetName) {
		try {
			switch (type) {
			case CSS:
				this.pageContext.getOut().append(getCssLink(targetName));
				break;
			case JS:
				this.pageContext.getOut().append(getJsLink(targetName));
				break;
			}

		} catch (Exception ex) {
			// Do nothing
		}
	}

	/**
	 * 
	 * @param fileName
	 * @return css file link
	 */
	private String getCssLink(String fileName) {
		String url = "";
		if (!debug) {
			url = Constants.TARGET_URL;
		}

		return String.format("<link href='%s/%s/%s' rel='stylesheet' media='all' type='text/css' />\n", this.pageContext
				.getServletContext().getContextPath(), url, fileName);

	}

	/**
	 * 
	 * @param fileName
	 * @return js file link
	 */
	private String getJsLink(String fileName) {
		String url = "";
		if (!debug) {
			url = Constants.TARGET_URL;
		}
		return String.format("<script src='%s/%s/%s'></script>\n", this.pageContext.getServletContext().getContextPath(), url,
				fileName);
	}

	/**
	 * 
	 * @param files
	 * @param type
	 * @param fileName
	 */
	private void checkWriteMinFile(List<String> files, FileType type, String fileName) {

		final String catalinaBase = this.pageContext.getServletContext().getRealPath("") + File.separatorChar;
		final File targetFile = new File(catalinaBase + Constants.BASE_PATH_MIN + File.separatorChar + fileName);

		try {

			new File(catalinaBase).mkdirs();

			if (targetFile.exists()) {
				targetFile.delete();
			}

			targetFile.createNewFile();

			final BufferedWriter writter = new BufferedWriter(new FileWriter(targetFile));
			for (String sourceFileName : files) {

				writter.append("\n\n");
				writter.append("/* " + sourceFileName + "*/");
				writter.append("\n\n");

				final String sourcePath = catalinaBase + Constants.BASE_PATH + File.separatorChar + sourceFileName;
				final BufferedReader reader = new BufferedReader(new FileReader(sourcePath));

				switch (type) {
				case CSS:
					compressCss(writter, reader);
					break;
				case JS:
					compressJs(writter, reader);
					break;

				}
				reader.close();
			}
			writter.flush();
			writter.close();

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * 
	 * @param writer
	 * @param reader
	 * @throws IOException
	 */
	private void compressCss(Writer writer, Reader reader) throws IOException {
		final CssCompressor compressor = new CssCompressor(reader);
		compressor.compress(writer, 0);

	}

	/**
	 * 
	 * @param writer
	 * @param reader
	 * @throws IOException
	 */
	private void compressJs(Writer writer, Reader reader) throws IOException {

		try {
			JavaScriptCompressor compressor = new JavaScriptCompressor(reader, new SystemOutErrorReporter());
			compressor.compress(writer, -1, true, false, false, false);
		} catch (Exception ex) {
			writer.append("\n/*");
			writer.append(ex.getMessage());
			writer.append("*/\n");
			reader.reset();
			writer.append(IOUtils.toString(reader));
			LOG.severe("ERROR: " + ex.getMessage());
		}
	}

	/**
	 * 
	 * @param base
	 * @return
	 */
	private String getMD5Name(String base) {
		try {
			MessageDigest m = MessageDigest.getInstance("MD5");
			m.update(base.getBytes(), 0, base.length());
			return new BigInteger(1, m.digest()).toString(16).toString();
		} catch (NoSuchAlgorithmException ex) {
			return base;
		}

	}

	/**
	 * 
	 * @author ehab
	 *
	 */
	class SystemOutErrorReporter implements ErrorReporter {

		private String format(String arg0, String arg1, int arg2, String arg3, int arg4) {
			return String.format("%s%s at line %d, column %d:\n%s", arg0, arg1 == null ? "" : ":" + arg1, arg2, arg4, arg3);
		}

		@Override
		public void warning(String arg0, String arg1, int arg2, String arg3, int arg4) {
			LOG.warning("WARNING: " + format(arg0, arg1, arg2, arg3, arg4));
		}

		@Override
		public void error(String arg0, String arg1, int arg2, String arg3, int arg4) {
			LOG.severe("ERROR: " + format(arg0, arg1, arg2, arg3, arg4));
		}

		@Override
		public EvaluatorException runtimeError(String arg0, String arg1, int arg2, String arg3, int arg4) {
			LOG.severe("RUNTIME ERROR: " + format(arg0, arg1, arg2, arg3, arg4));
			return new EvaluatorException(arg0);
		}
	}
}

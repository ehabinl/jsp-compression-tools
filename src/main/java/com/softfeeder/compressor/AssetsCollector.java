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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * 
 * @author ehab al-hakawati
 * @since 1.0
 *
 */
public class AssetsCollector extends TagSupport {

	private static final long serialVersionUID = 5071731143056606938L;

	private String src;
	private String collection = "header";

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.jsp.tagext.TagSupport#doStartTag()
	 */
	@Override
	@SuppressWarnings({ "unchecked" })
	public int doStartTag() throws JspException {

		Map<FileType, Map<String, List<String>>> resources = (Map<FileType, Map<String, List<String>>>) this.pageContext
				.getAttribute(Constants.TAG_LIB_MAP_VALUE_KEY, PageContext.REQUEST_SCOPE);

		if (resources == null) {
			resources = new HashMap<FileType, Map<String, List<String>>>();
			this.pageContext.setAttribute(Constants.TAG_LIB_MAP_VALUE_KEY, resources, PageContext.REQUEST_SCOPE);
		}

		if (this.src.toLowerCase().endsWith(FileType.CSS.getExtention())) {
			addResource(resources, FileType.CSS);
		} else if (this.src.toLowerCase().endsWith(FileType.JS.getExtention())) {
			addResource(resources, FileType.JS);
		} else {
			throw new JspException("Unknown file type");
		}

		return SKIP_BODY;
	}

	/**
	 * 
	 * @param resources
	 * @param fileType
	 */
	private void addResource(Map<FileType, Map<String, List<String>>> resources, FileType fileType) {

		Map<String, List<String>> locationMap = resources.get(fileType);
		if (locationMap == null) {
			locationMap = new HashMap<String, List<String>>();
			resources.put(fileType, locationMap);
		}

		String location = String.valueOf(this.collection.toUpperCase());
		List<String> files = locationMap.get(location);

		if (files == null) {
			files = new LinkedList<String>();
			locationMap.put(location, files);
		}

		if (!files.contains(this.src)) {
			files.add(this.src);
		}
	}
}

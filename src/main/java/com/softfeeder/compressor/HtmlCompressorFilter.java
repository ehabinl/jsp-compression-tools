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

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.logging.Logger;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import com.googlecode.htmlcompressor.compressor.HtmlCompressor;

/**
 * 
 * @author ehab al-hakawati
 * @since 1.0
 *
 */
public class HtmlCompressorFilter implements Filter {

	private static final Logger LOG = Logger.getLogger(HtmlCompressorFilter.class.getName());

	/*
	 * (non-Javadoc)
	 * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
	 */
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
			ServletException {

		// -- wrappers
		final HttpServletResponse httpResponse = (HttpServletResponse) response;
		final GenericResponseWrapper responseWrapper = new GenericResponseWrapper((HttpServletResponse) response);
		final OutputStream outputStream = httpResponse.getOutputStream();

		chain.doFilter(request, responseWrapper);

		final String contentType = response.getContentType();

		if (!Constants.DEBUG_MOOD && contentType != null && contentType.toLowerCase().contains("html")) {
			// -- not a debug run + html content ---> Compress
			final HtmlCompressor compressor = new HtmlCompressor();
			compressor.setCompressCss(true);
			compressor.setCompressJavaScript(true);
			try {
				outputStream.write(compressor.compress(responseWrapper.toString()).getBytes("UTF-8"));
			} catch (Exception ex) {
				LOG.severe(String.format("Can't compress html %s : %s", response.getContentType(), ex.getMessage()));
				outputStream.write(responseWrapper.getBytes());
			}
		} else {
			outputStream.write(responseWrapper.getBytes());
		}

		outputStream.close();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
	 */
	@Override
	public void init(FilterConfig config) throws ServletException {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.Filter#destroy()
	 */
	@Override
	public void destroy() {
	}

	/**
	 * 
	 * @author ehab
	 * @since 1.0
	 */
	private class GenericResponseWrapper extends HttpServletResponseWrapper {
		private CharArrayWriter output;

		/**
		 * 
		 * @param response
		 */
		public GenericResponseWrapper(HttpServletResponse response) {
			super(response);
			this.output = new CharArrayWriter();
		}

		/**
		 * 
		 * @return output char array
		 */
		public byte[] getBytes() {
			return toBytes(this.output.toCharArray());
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.servlet.ServletResponseWrapper#getWriter()
		 */
		@Override
		public PrintWriter getWriter() {
			return new PrintWriter(output, true);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return output.toString();
		}

		/**
		 * 
		 * @param chars
		 * @return new byte array
		 */
		private byte[] toBytes(char[] chars) {
			CharBuffer charBuffer = CharBuffer.wrap(chars);
			ByteBuffer byteBuffer = Charset.forName("UTF-8").encode(charBuffer);
			byte[] bytes = Arrays.copyOfRange(byteBuffer.array(), byteBuffer.position(), byteBuffer.limit());
			Arrays.fill(charBuffer.array(), '\u0000'); // clear sensitive data
			Arrays.fill(byteBuffer.array(), (byte) 0); // clear sensitive data
			return bytes;
		}
	}
}

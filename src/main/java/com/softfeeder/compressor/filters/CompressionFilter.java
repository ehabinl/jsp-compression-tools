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
package com.softfeeder.compressor.filters;

import java.io.*;
import java.util.logging.Logger;

import javax.servlet.*;
import javax.servlet.http.*;

import com.softfeeder.compressor.filters.wrapper.CompressedResponseWrapper;

/**
 * 
 * @author ehab al-hakawati
 * @since 1.0
 *
 */
public class CompressionFilter implements Filter {

	private static final Logger LOG = Logger.getLogger(CompressionFilter.class.getName());

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest,
	 * javax.servlet.ServletResponse, javax.servlet.FilterChain)
	 */
	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {

		// -- check if http request
		if (req instanceof HttpServletRequest) {

			HttpServletRequest request = (HttpServletRequest) req;
			HttpServletResponse response = (HttpServletResponse) res;

			// -- get client accept-encoding
			String acceptEncoding = request.getHeader("accept-encoding");

			if (acceptEncoding != null && acceptEncoding.contains("gzip")) {
				// -- gzip wrapper
				LOG.fine(String.format("serve request with gzip filter %s", request.getRequestURI()));
				CompressedResponseWrapper wrappedResponse = new CompressedResponseWrapper(response, "gzip");
				
				chain.doFilter(req, wrappedResponse);
				
				wrappedResponse.close();
			} else if (acceptEncoding != null && acceptEncoding.contains("deflate")) {
				// -- deflate wrapper
				LOG.fine(String.format("serve request with deflate filter %s", request.getRequestURI()));
				CompressedResponseWrapper wrappedResponse = new CompressedResponseWrapper(response, "deflate");
				
				chain.doFilter(req, wrappedResponse);
				
				wrappedResponse.close();
			} else {
				chain.doFilter(req, res);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
	 */
	@Override
	public void init(FilterConfig filterConfig) {
		// -- nothing to setup
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.Filter#destroy()
	 */
	@Override
	public void destroy() {
		// -- nothing to destroy
	}
}

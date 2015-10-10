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
package com.softfeeder.compressor.filters.wrapper;

import java.io.IOException;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPOutputStream;

import javax.servlet.http.HttpServletResponse;

/**
 * 
 * @author ehab al-hakawati
 * @since 1.0
 *
 */
public class GZIPResponseStream extends CompressedResponseStream {

	final private GZIPOutputStream gzipStream;

	/**
	 * Constructor
	 * 
	 * @param response
	 * @throws IOException
	 */
	public GZIPResponseStream(HttpServletResponse response) throws IOException {
		super(response);
		this.gzipStream = new GZIPOutputStream(this.baos);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.softfeeder.compressor.filters.wrapper.CompressedResponseStream#
	 * getCompressedStream()
	 */
	@Override
	public DeflaterOutputStream getCompressedStream() {
		return this.gzipStream;
	}

}

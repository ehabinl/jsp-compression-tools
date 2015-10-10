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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.DeflaterOutputStream;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletResponse;

/**
 * 
 * @author ehab al-hakawati
 * @since 1.0
 *
 */
public abstract class CompressedResponseStream extends ServletOutputStream {

	final private HttpServletResponse response;
	final private ServletOutputStream output;
	final protected ByteArrayOutputStream baos;

	private boolean closed = false;

	/**
	 * Constructor
	 * 
	 * @param response
	 * @throws IOException
	 */
	public CompressedResponseStream(HttpServletResponse response) throws IOException {
		super();
		this.response = response;
		this.output = response.getOutputStream();
		this.baos = new ByteArrayOutputStream();
	}

	/**
	 * finish and write compressed stream
	 */
	public void close() throws IOException {
		if (closed) {
			throw new IOException("This output stream has already been closed");
		}
		getCompressedStream().finish();

		byte[] bytes = baos.toByteArray();

		response.addHeader("Content-Length", Integer.toString(bytes.length));
		response.addHeader("Content-Encoding", "gzip");
		output.write(bytes);
		output.flush();
		output.close();
		closed = true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.OutputStream#flush()
	 */
	@Override
	public void flush() throws IOException {
		if (closed) {
			throw new IOException("Cannot flush a closed output stream");
		}
		getCompressedStream().flush();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.OutputStream#write(int)
	 */
	@Override
	public void write(int b) throws IOException {
		if (closed) {
			throw new IOException("Cannot write to a closed output stream");
		}
		getCompressedStream().write(b);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.OutputStream#write(byte[])
	 */
	@Override
	public void write(byte b[]) throws IOException {
		write(b, 0, b.length);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.OutputStream#write(byte[], int, int)
	 */
	@Override
	public void write(byte b[], int off, int len) throws IOException {
		if (closed) {
			throw new IOException("Cannot write to a closed output stream");
		}
		getCompressedStream().write(b, off, len);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.ServletOutputStream#isReady()
	 */
	@Override
	public boolean isReady() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.ServletOutputStream#setWriteListener(javax.servlet.
	 * WriteListener)
	 */
	@Override
	public void setWriteListener(WriteListener writeListener) {
		// -- nothing to do
	}

	/**
	 * 
	 * @return deflater stream (gzip/deflate)
	 */
	public abstract DeflaterOutputStream getCompressedStream();

	/**
	 * Factory method
	 * 
	 * @param response
	 * @param algo
	 * @return CompressedResponseStream implementation
	 * @throws IOException
	 */
	public static CompressedResponseStream getInstance(HttpServletResponse response, String algo) throws IOException {
		switch (algo) {
		case "gzip":
			return new GZIPResponseStream(response);

		case "deflate":
			return new DeflateResponseStream(response);
		}
		return null;
	}
}

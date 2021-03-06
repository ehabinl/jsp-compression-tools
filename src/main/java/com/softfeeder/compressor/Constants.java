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

import java.util.ResourceBundle;

/**
 * 
 * @author ehab al-hakawati
 * @since 1.0
 *
 */
public class Constants {
	
	// -- context init time
	public static final long START_TIME = System.currentTimeMillis();

	public static final String TAG_LIB_MAP_VALUE_KEY = "SOFTFEEDER_TAGLIB_COMPRESS_APPENDER_VALUE";
	public static final String PROPERTYFILE_NAME = "softfeeder_compress_taglib";
	public static final String PROPERTYFILE_LOCATION = "softfeeder.bundle.location";
	public static final String BASE_PATH_MIN;
	public static final String BASE_PATH;
	public static final String TARGET_URL;
	public static final boolean DEBUG_MOOD;

	static {
		String platform = System.getProperty(Constants.PROPERTYFILE_LOCATION) == null ? "config" : System
				.getProperty(Constants.PROPERTYFILE_LOCATION);

		ResourceBundle rs = ResourceBundle.getBundle(platform + "/" + Constants.PROPERTYFILE_NAME);
		BASE_PATH_MIN = rs.getString("softfeeder.compress.basePathMin");
		BASE_PATH = rs.getString("softfeeder.compress.basePath");
		TARGET_URL = rs.getString("softfeeder.compress.targetUrl");
		DEBUG_MOOD = Boolean.valueOf(rs.getString("softfeeder.compress.debug"));

	}

}

/************************************************************************ 
 * Copyright MasterChef
 * ============================================= 
 *          Auther: Ehab Al-Hakawati 
 *              on: Oct 7, 2015 1:16:51 PM
 */
package com.softfeeder.compressor.taglib;

public enum FileType {
	CSS("css"), JS("js");

	private String extention;

	FileType(String extension) {
		this.extention = extension;
	}

	public String getExtention() {
		return this.extention;
	}
}

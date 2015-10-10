/************************************************************************ 
 * Copyright MasterChef
 * ============================================= 
 *          Auther: Ehab Al-Hakawati 
 *              on: Oct 7, 2015 12:27:08 PM
 */
package com.softfeeder.compressor.taglib;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.TagSupport;

import com.softfeeder.compressor.Constants;

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

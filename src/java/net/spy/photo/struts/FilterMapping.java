// Copyright (c) 2004  Dustin Sallings <dustin@spy.net>
// arch-tag: 1F2AFEB3-5D6E-11D9-8F24-000A957659CC

package net.spy.photo.struts;

import org.apache.struts.action.ActionMapping;

/**
 * Struts mapping for specifying a filter.
 */
public class FilterMapping extends ActionMapping {

	private String filterClass=null;

	/**
	 * Get an instance of FilterMapping.
	 */
	public FilterMapping() {
		super();
	}

	/** 
	 * Set the filter class to use.
	 */
	public void setFilterClass(String to) {
		this.filterClass=to;
	}

	/** 
	 * Get the filter class to use.
	 */
	public String getFilterClass() {
		return(filterClass);
	}

}
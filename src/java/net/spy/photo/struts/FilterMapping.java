// Copyright (c) 2004  Dustin Sallings <dustin@spy.net>

package net.spy.photo.struts;

import org.apache.struts.action.ActionMapping;

/**
 * Struts mapping for specifying a filter.
 */
public class FilterMapping extends ActionMapping {

	private String filterClass=null;

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

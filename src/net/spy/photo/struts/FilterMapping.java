// Copyright (c) 2004  Dustin Sallings <dustin@spy.net>

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
	public void setFilterClass(String filterClass) {
		this.filterClass=filterClass;
	}

	/** 
	 * Get the filter class to use.
	 */
	public String getFilterClass() {
		return(filterClass);
	}

}

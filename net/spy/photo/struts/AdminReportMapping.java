// Copyright (c) 2003  Dustin Sallings <dustin@spy.net>
//
// $Id: AdminReportMapping.java,v 1.1 2003/05/11 09:24:21 dustin Exp $

package net.spy.photo.struts;

import org.apache.struts.action.ActionMapping;

/**
 * ActionMapping for reporting stuff.
 */
public class AdminReportMapping extends ActionMapping {

	private String reportName=null;
	private String spt=null;
	private Class sptClass=null;

	/**
	 * Get an instance of AdminReportMapping.
	 */
	public AdminReportMapping() {
		super();
	}

	/** 
	 * Get the name of this report.
	 */
	public String getReportName() {
		return(reportName);
	}

	/** 
	 * Set the name of this report.
	 */
	public void setReportName(String reportName) {
		this.reportName=reportName;
	}

	/** 
	 * Get the SPT class name.
	 */
	public String getSpt() {
		return(spt);
	}

	/** 
	 * Set the SPT class name.
	 */
	public void setSpt(String spt) {
		try {
			this.sptClass=Class.forName(spt);
		} catch(ClassNotFoundException e) {
			throw new RuntimeException("Can't load " + spt);
		}
		this.spt=spt;
	}

	/** 
	 * Get the SPT class.
	 */
	public Class getSptClass() {
		return(sptClass);
	}

}

// Copyright (c) 2003  Dustin Sallings <dustin@spy.net>
//
// $Id: AdminReportAction.java,v 1.8 2003/12/02 03:34:58 dustin Exp $

package net.spy.photo.struts;

import net.spy.SpyConfig;

import net.spy.jwebkit.struts.DBSPAction;

import net.spy.photo.PhotoConfig;

/**
 * Report fetch action.
 */
public class AdminReportAction extends DBSPAction {

	/**
	 * Get an instance of AdminReportAction.
	 */
	public AdminReportAction() {
		super();
	}

	/** 
	 * Get the PhotoConfig.
	 */
	protected SpyConfig getSpyConfig() {
		return(new PhotoConfig());
	}

}

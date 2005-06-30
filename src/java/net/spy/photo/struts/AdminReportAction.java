// Copyright (c) 2003  Dustin Sallings <dustin@spy.net>
// arch-tag: 0C7D3359-5D6E-11D9-8A42-000A957659CC

package net.spy.photo.struts;

import org.apache.struts.action.ActionMapping;

import net.spy.jwebkit.struts.DBSPAction;
import net.spy.photo.PhotoConfig;
import net.spy.util.SpyConfig;

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
	protected SpyConfig getSpyConfig(ActionMapping mapping) {
		return(PhotoConfig.getInstance());
	}

}

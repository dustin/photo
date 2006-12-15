// Copyright (c) 2003  Dustin Sallings <dustin@spy.net>

package net.spy.photo.struts;

import net.spy.jwebkit.struts.DBSPAction;
import net.spy.photo.PhotoConfig;
import net.spy.util.SpyConfig;

import org.apache.struts.action.ActionMapping;

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
	@Override
	protected SpyConfig getSpyConfig(ActionMapping mapping) {
		return(PhotoConfig.getInstance());
	}

}

// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: InitSessionDataExtraInfo.java,v 1.2 2002/07/10 03:38:09 dustin Exp $

package net.spy.photo.taglib;

import javax.servlet.jsp.tagext.TagData;
import javax.servlet.jsp.tagext.TagExtraInfo;
import javax.servlet.jsp.tagext.VariableInfo;

/**
 * Variables that get set when the session data is requested.
 */
public class InitSessionDataExtraInfo extends TagExtraInfo {

	/**
	 * Get the variable info
	 */
	public VariableInfo[] getVariableInfo(TagData data) {
		return(new VariableInfo[] {
			new VariableInfo(
				"sessionData", "net.spy.photo.PhotoSessionData", true,
				VariableInfo.AT_BEGIN)});
	}

}

// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: InitSessionDataExtraInfo.java,v 1.1 2002/05/07 08:38:49 dustin Exp $

package net.spy.photo.taglib;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;

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

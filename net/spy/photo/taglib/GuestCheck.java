// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: GuestCheck.java,v 1.3 2002/05/23 06:54:51 dustin Exp $

package net.spy.photo.taglib;

import javax.servlet.*;
import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;

/**
 * Allow simple conditionals based on whether the user is logged in or
 * guest.
 */
public class GuestCheck extends ConditionalTag {

	/**
	 * Get an instance of BaseUrl.
	 */
	public GuestCheck() {
		super();
	}

	/**
	 * If the user is guest and negate is false, process the body.
	 */
	public int doStartTag() throws JspException {
		// Figure out the username
		boolean isGuest=getSessionData().getUser().getUsername().equals("guest");

		// Get the return value based on this truth.
		int rv=getReturnValue(isGuest);

		return rv;
	}
}

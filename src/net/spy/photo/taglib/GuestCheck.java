// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: GuestCheck.java,v 1.4 2002/07/10 03:38:09 dustin Exp $

package net.spy.photo.taglib;

import javax.servlet.jsp.JspException;

import net.spy.photo.PhotoUser;

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
		// Get the user
		PhotoUser user=getSessionData().getUser();
		// true if the user is named ``guest''
		boolean isGuest=user.getUsername().equals("guest");

		// Get the return value based on this truth.
		int rv=getReturnValue(isGuest);

		return rv;
	}
}

// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: GuestCheck.java,v 1.2 2002/05/15 08:28:06 dustin Exp $

package net.spy.photo.taglib;

import javax.servlet.*;
import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;

import net.spy.photo.*;

/**
 * Allow simple conditionals based on whether the user is logged in or
 * guest.
 */
public class GuestCheck extends PhotoTag {

	private boolean negate=false;

	/**
	 * Get an instance of BaseUrl.
	 */
	public GuestCheck() {
		super();
	}

	/**
	 * Set the arguments for the URL.
	 */
	public void setNegate(String to) {
		this.negate=true;
	}

	/**
	 * Provide a link to the backend servlet.
	 */
	public int doStartTag() throws JspException {
		boolean isGuest=true;
		int rv=0;

		// Figure out the username
		PhotoSessionData sessionData=(PhotoSessionData)
			pageContext.getAttribute("sessionData", PageContext.REQUEST_SCOPE);

		isGuest=sessionData.getUser().getUsername().equals("guest");

		if(isGuest) {
			rv=negate?SKIP_BODY:EVAL_BODY_INCLUDE;
		} else {
			rv=negate?EVAL_BODY_INCLUDE:SKIP_BODY;
		}

		return rv;
	}
}

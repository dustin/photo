// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: AddCheck.java,v 1.1 2002/05/23 16:17:18 dustin Exp $

package net.spy.photo.taglib;

import javax.servlet.*;
import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;

/**
 * Conditional based on a user's canAdd flag.
 */
public class AddCheck extends ConditionalTag {

	/**
	 * Get an instance of AddCheck.
	 */
	public AddCheck() {
		super();
	}

	/**
	 * If the user can add and negate is false, process the body.
	 */
	public int doStartTag() throws JspException {
		// Find out if the user can add.
		boolean canAdd=getSessionData().getUser().canAdd();
		// Get the return value based on this truth.
		int rv=getReturnValue(canAdd);

		return(rv);
	}

}

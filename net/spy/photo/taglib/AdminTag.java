// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//  
// $Id: AdminTag.java,v 1.1 2002/05/24 00:48:41 dustin Exp $

package net.spy.photo.taglib;

import javax.servlet.*;
import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;

import net.spy.photo.*;

/**
 * Conditional taglib based on user's administrative flag.
 *
 * If used with no arguments, this tag will evaluate the body if the user's
 * admin flag is ADMIN.  More later.
 */
public class AdminTag extends ConditionalTag {

	/**
	 * Get an instance of AdminTag.
	 */
	public AdminTag() {
		super();
	}

	/**
	 * Do something based on the user's admin flag.
	 */
	public int doStartTag() throws JspException {
		// True if the ADMIN flag is set.
		boolean b=getSessionData().checkAdminFlag(PhotoSessionData.ADMIN);
		// Get the return value
		int rv=getReturnValue(b);

		return(rv);
	}

}

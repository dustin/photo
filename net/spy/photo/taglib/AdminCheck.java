// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//  
// $Id: AdminCheck.java,v 1.3 2002/06/22 05:58:12 dustin Exp $

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
public class AdminCheck extends ConditionalTag {

	private boolean explodeOnImpact=false;

	/**
	 * Get an instance of AdminTag.
	 */
	public AdminCheck() {
		super();
	}

	/**
	 * If true, explode on doStartTag() instead of returning false.  This
	 * prevents admin pages from being served up to unworty beings.
	 */
	public void setExplodeOnImpact(boolean explodeOnImpact) {
		this.explodeOnImpact=explodeOnImpact;
	}

	/**
	 * If true, explode on doStartTag() instead of returning false.  This
	 * prevents admin pages from being served up to unworty beings.
	 */
	public void setExplodeOnImpact(String explodeOnImpact) {
		Boolean b=new Boolean(explodeOnImpact);
		this.explodeOnImpact=b.booleanValue();
	}

	/**
	 * Do something based on the user's admin flag.
	 */
	public int doStartTag() throws JspException {
		// True if the ADMIN flag is set.
		boolean b=getSessionData().checkAdminFlag(PhotoSessionData.ADMIN);
		// Get the return value
		int rv=getReturnValue(b);

		if(b == false && explodeOnImpact == true) {
			throw new JspException("User is not admin.");
		}

		return(rv);
	}

}

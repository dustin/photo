// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
// arch-tag: 45FAC458-5D6E-11D9-A56E-000A957659CC

package net.spy.photo.taglib;

import javax.servlet.jsp.JspException;

import net.spy.photo.PhotoSessionData;

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
		this.explodeOnImpact=Boolean.valueOf(explodeOnImpact).booleanValue();
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

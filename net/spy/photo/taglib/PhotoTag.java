// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: PhotoTag.java,v 1.2 2002/06/11 00:35:01 dustin Exp $

package net.spy.photo.taglib;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;

import net.spy.photo.*;

/**
 * Superclass for all taglibs.
 */
public abstract class PhotoTag extends TagSupport {

	private PhotoSessionData sessionData=null;

	/**
	 * Get an instance of PhotoTag.
	 */
	public PhotoTag() {
		super();
	}

	/**
	 * Get the session data object.
	 */
	protected PhotoSessionData getSessionData() throws JspException {
		if(sessionData==null) {
			HttpSession session=pageContext.getSession();
			sessionData=(PhotoSessionData)session.getAttribute("photoSession");

			if(sessionData==null) {
				throw new JspException("photoSession not in session.");
			}
		}

		return(sessionData);
	}

}

// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: PhotoTag.java,v 1.4 2002/06/28 03:57:22 dustin Exp $

package net.spy.photo.taglib;

import java.util.*;
import java.text.*;

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

	/**
	 * Get the application resource bundle.
	 */
	protected ResourceBundle getResourceBundle() {
		Locale l=pageContext.getRequest().getLocale();
		ResourceBundle rb=ResourceBundle.getBundle(
			"net.spy.photo.photoresources", l);
		return(rb);
	}

}

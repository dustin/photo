// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: PhotoAction.java,v 1.5 2003/07/31 08:03:42 dustin Exp $

package net.spy.photo.struts;

import javax.servlet.ServletException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import net.spy.jwebkit.struts.JWebAction;

import net.spy.photo.PhotoSessionData;

/**
 * Superclass for all PhotoAction classes.
 */
public abstract class PhotoAction extends JWebAction {

	/**
	 * Get an instance of PhotoAction.
	 */
	public PhotoAction() {
		super();
	}

	/**
	 * Get the sessionData from the session.
	 */
	protected PhotoSessionData getSessionData(HttpServletRequest request)
		throws ServletException {

		HttpSession session=request.getSession(false);
		PhotoSessionData sessionData=
			(PhotoSessionData)session.getAttribute(PhotoSessionData.SES_ATTR);

		if(sessionData==null) {
			throw new ServletException("No photoSession in session.");
		}

		return(sessionData);
	}

}

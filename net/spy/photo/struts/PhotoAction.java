// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: PhotoAction.java,v 1.3 2002/07/10 03:38:09 dustin Exp $

package net.spy.photo.struts;

import javax.servlet.ServletException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import net.spy.photo.PhotoSessionData;

import org.apache.struts.action.Action;

/**
 * Superclass for all PhotoAction classes.
 */
public abstract class PhotoAction extends Action {

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
			(PhotoSessionData)session.getAttribute("photoSession");

		if(sessionData==null) {
			throw new ServletException("No photoSession in session.");
		}

		return(sessionData);
	}

	/**
	 * Log a message.
	 */
	protected void log(String msg) {
		System.err.println(getClass().getName() + " - " + msg);
	}

}

// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: PhotoAction.java,v 1.1 2002/05/13 07:22:48 dustin Exp $

package net.spy.photo.struts;

import javax.servlet.*;
import javax.servlet.http.*;

import org.apache.struts.action.*;

import net.spy.photo.*;

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

}

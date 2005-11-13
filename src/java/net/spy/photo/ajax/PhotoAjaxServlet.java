// Copyright (c) 2005  Dustin Sallings <dustin@spy.net>
// arch-tag: C0FC149A-B6F6-4B72-8AFF-5DC9F81571DC

package net.spy.photo.ajax;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import net.spy.jwebkit.AjaxServlet;

import net.spy.photo.PhotoSessionData;

/**
 * Base class for photo ajax requests.
 */
public abstract class PhotoAjaxServlet extends AjaxServlet {

	/** 
	 * Get the sessionData for this session.
	 */
	protected PhotoSessionData getSessionData(HttpServletRequest request)
		throws ServletException {
		HttpSession session=request.getSession(false);
		PhotoSessionData sessionData=
			(PhotoSessionData)session.getAttribute(PhotoSessionData.SES_ATTR);
		if(sessionData == null) {
			throw new ServletException("No photoSession in session.");
		}
		return(sessionData);
	}

}

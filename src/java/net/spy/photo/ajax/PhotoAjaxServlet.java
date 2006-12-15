// Copyright (c) 2005  Dustin Sallings <dustin@spy.net>

package net.spy.photo.ajax;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import net.spy.jwebkit.AjaxServlet;
import net.spy.photo.PhotoSessionData;
import net.spy.photo.User;
import net.spy.xml.SAXAble;

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

	/**
	 * Get the current user.
	 */
	protected User getUser(HttpServletRequest request) throws ServletException {
		return(getSessionData(request).getUser());
	}
	
	/** 
	 * Default saxable - returns null.
	 */
	@Override
	protected SAXAble getResults(HttpServletRequest request) throws Exception {
		return(null);
	}

}

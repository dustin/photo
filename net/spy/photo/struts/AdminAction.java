// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: AdminAction.java,v 1.1 2002/06/22 23:27:45 dustin Exp $

package net.spy.photo.struts;

import javax.servlet.*;
import javax.servlet.http.*;

import org.apache.struts.action.*;

import net.spy.photo.*;

/**
 * Administrative actions fall under this class.
 */
public abstract class AdminAction extends PhotoAction {

	/**
	 * Get an instance of AdminAction.
	 */
	public AdminAction() {
		super();
	}

	/**
	 * Verify the user has the admin flag set.
	 */
	protected void checkAdmin(HttpServletRequest request)
		throws ServletException {

		PhotoSessionData sessionData=getSessionData(request);
		
		if(!sessionData.checkAdminFlag(PhotoSessionData.ADMIN)) {
			throw new ServletException("Not admin.");
		}
	}

	/**
	 * Verify the user has the subadmin flag set.
	 */
	protected void checkSubAdmin(HttpServletRequest request)
		throws ServletException {

		PhotoSessionData sessionData=getSessionData(request);

		if(!sessionData.checkAdminFlag(PhotoSessionData.SUBADMIN)) {
			throw new ServletException("Not subadmin.");
		}
	}

	/**
	 * Verify the user has either the admin or subadmin flag set.
	 */
	protected void checkAdminOrSubadmin(HttpServletRequest request) 
		throws ServletException {

		PhotoSessionData sessionData=getSessionData(request);

		if( (!sessionData.checkAdminFlag(PhotoSessionData.ADMIN))
			&& (!sessionData.checkAdminFlag(PhotoSessionData.SUBADMIN))) {

			throw new ServletException("Not admin or subadmin.");
		}
	}

}

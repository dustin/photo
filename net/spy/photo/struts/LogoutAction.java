// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: LogoutAction.java,v 1.4 2003/07/14 06:21:28 dustin Exp $

package net.spy.photo.struts;

import java.io.IOException;

import javax.servlet.ServletException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * Log the current session out.
 */
public class LogoutAction extends PhotoAction {

	/**
	 * Get an instance of LogoutAction.
	 */
	public LogoutAction() {
		super();
	}

	/**
	 * Process the request.
	 */
	public ActionForward execute(ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,HttpServletResponse response)
		throws Exception {

		// Throw away the whole session.
		HttpSession session=request.getSession();
		session.invalidate();

		return(mapping.findForward("next"));
	}

}

// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: LogoutAction.java,v 1.1 2002/06/04 07:16:03 dustin Exp $

package net.spy.photo.struts;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.apache.struts.action.*;

import net.spy.photo.*;

/**
 * Validate user credentials and perform a login.
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
	public ActionForward perform(ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,HttpServletResponse response)
		throws IOException, ServletException {

		// Throw away the whole session.
		HttpSession session=request.getSession();
		session.invalidate();

		return(mapping.findForward("success"));
	}

}

// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: LoginAction.java,v 1.1 2002/05/08 10:03:42 dustin Exp $

package net.spy.photo.struts;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.apache.struts.action.*;

/**
 * Validate user credentials and perform a login.
 */
public class LoginAction extends Action {

	/**
	 * Get an instance of LoginAction.
	 */
	public LoginAction() {
		super();
	}

	/**
	 * Process the request.
	 */
	public ActionForward perform(ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,HttpServletResponse response)
		throws IOException, ServletException {

		LoginForm lf=(LoginForm)form;
		
		System.out.println("Attempting a login as " + lf.getUsername());

		// Go ahead and say it's alright.
		return (mapping.findForward("success"));
	}

}

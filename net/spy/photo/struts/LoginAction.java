// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: LoginAction.java,v 1.4 2002/05/17 06:20:56 dustin Exp $

package net.spy.photo.struts;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.apache.struts.action.*;

import net.spy.photo.*;

/**
 * Validate user credentials and perform a login.
 */
public class LoginAction extends PhotoAction {

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

		ActionForward rv=null;

		LoginForm lf=(LoginForm)form;

		PhotoSessionData sessionData=getSessionData(request);

		PhotoUser user=Persistent.security.getUser(lf.getUsername());
		if(user==null) {
			throw new ServletException(
				"Your username or password is incorrect.");
		}

		if(user.checkPassword(lf.getPassword())) {
			sessionData.setUser(user);
			sessionData.setAdmin(PhotoSessionData.NOADMIN);

			PhotoLogEntry ple=new PhotoLogEntry(user.getId(), "Login",request);
			Persistent.logger.log(ple);
		} else {
			PhotoLogEntry ple=new PhotoLogEntry(
				user.getId(), "AuthFail", request);
			Persistent.logger.log(ple);
			throw new ServletException(
				"Your username or password is incorrect.");
		}

		rv=mapping.findForward("success");

		// Go ahead and say it's alright.
		return(rv);
	}

}

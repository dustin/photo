// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: LoginAction.java,v 1.11 2003/01/07 09:38:53 dustin Exp $

package net.spy.photo.struts;

import java.io.IOException;

import javax.servlet.ServletException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.spy.photo.Persistent;
import net.spy.photo.PhotoLogEntry;
import net.spy.photo.PhotoSessionData;
import net.spy.photo.PhotoUser;
import net.spy.photo.PhotoUserException;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

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

		PhotoUser user=null;
		try {
			user=Persistent.getSecurity().getUser(lf.getUsername());
		} catch(PhotoUserException e) {
			throw new ServletException(
				"Your username or password is incorrect.");
		}

		if(user.checkPassword(lf.getPassword())) {
			sessionData.setUser(user);

			PhotoLogEntry ple=new PhotoLogEntry(user.getId(), "Login",request);
			Persistent.getLogger().log(ple);
		} else {
			PhotoLogEntry ple=new PhotoLogEntry(
				user.getId(), "AuthFail", request);
			Persistent.getLogger().log(ple);
			throw new ServletException(
				"Your username or password is incorrect.");
		}

		// Find out of the user wanted to upgrade to admin privs after 
		if(lf.getAdmin()) {
			System.err.println(user + " logged in as admin");
			rv=mapping.findForward("setadmin");
		} else {
			rv=mapping.findForward("success");
		}

		// Go ahead and say it's alright.
		return(rv);
	}

}

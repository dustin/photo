// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: LoginAction.java,v 1.14 2003/07/14 06:21:28 dustin Exp $

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
import org.apache.struts.action.DynaActionForm;

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
	public ActionForward execute(ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,HttpServletResponse response)
		throws Exception {

		ActionForward rv=null;

		DynaActionForm lf=(DynaActionForm)form;

		PhotoSessionData sessionData=getSessionData(request);

		PhotoUser user=Persistent.getSecurity().getUser(
			(String)lf.get("username"));

		if(user.checkPassword((String)lf.get("password"))) {
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
		Boolean bol=(Boolean)lf.get("admin");
		if(bol.booleanValue()) {
			System.err.println(user + " logged in as admin");
			rv=mapping.findForward("setadmin");
		} else {
			rv=mapping.findForward("next");
		}

		// Go ahead and say it's alright.
		return(rv);
	}

}

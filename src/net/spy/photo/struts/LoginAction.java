// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: LoginAction.java,v 1.15 2003/07/23 04:29:26 dustin Exp $

package net.spy.photo.struts;

import java.io.IOException;

import javax.servlet.ServletException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.spy.db.Saver;

import net.spy.util.PwGen;

import net.spy.photo.Persistent;
import net.spy.photo.PhotoLogEntry;
import net.spy.photo.PhotoSessionData;
import net.spy.photo.PhotoUser;
import net.spy.photo.PhotoUserException;
import net.spy.photo.PhotoConfig;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.DynaActionForm;

/**
 * Validate user credentials and perform a login.
 */
public class LoginAction extends PhotoAction {

	/** 
	 * Name of the persistent session cookie.
	 */
	public static final String PERSESS_COOKIE="persess";

	/**
	 * Get an instance of LoginAction.
	 */
	public LoginAction() {
		super();
	}

	private void persist(PhotoUser user,
		HttpServletRequest request,
		HttpServletResponse response) throws Exception {

		// Get a persistent session ID
		String persess=PwGen.getPass(16);

		// Add a cookie
		Cookie c=new Cookie(PERSESS_COOKIE, persess);
		// Let's keep it for about two months.
		c.setMaxAge(86400*30*2);
		response.addCookie(c);

		// Set the ID
		user.setPersess(persess);
		// Save the user
		Saver saver=new Saver(PhotoConfig.getInstance());
		saver.save(user);

		// Recache the users
		PhotoUser.recache();
	}

	/**
	 * Process the request.
	 */
	public ActionForward spyExecute(ActionMapping mapping,
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

			PhotoLogEntry ple=new PhotoLogEntry(user.getId(), "Login", request);
			Persistent.getPipeline().addTransaction(ple,
				PhotoConfig.getInstance());
		} else {
			PhotoLogEntry ple=new PhotoLogEntry(
				user.getId(), "AuthFail", request);
			Persistent.getPipeline().addTransaction(ple,
				PhotoConfig.getInstance());
			throw new ServletException(
				"Your username or password is incorrect.");
		}

		// Find out if the user wants to persist the login
		Boolean bol=(Boolean)lf.get("persist");
		if(bol.booleanValue()) {
			persist(user, request, response);
		} else {
			Cookie c=new Cookie(PERSESS_COOKIE, "delete");
			c.setMaxAge(0); // delete
			response.addCookie(c);
		}

		// Find out of the user wanted to upgrade to admin privs after 
		bol=(Boolean)lf.get("admin");
		if(bol.booleanValue()) {
			getLogger().info(user + " logged in as admin");
			rv=mapping.findForward("setadmin");
		} else {
			rv=mapping.findForward("next");
		}

		// Go ahead and say it's alright.
		return(rv);
	}

}

// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
// arch-tag: 2C18BEAD-5D6E-11D9-BA6D-000A957659CC

package net.spy.photo.struts;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.spy.photo.MutableUser;
import net.spy.photo.Persistent;
import net.spy.photo.PhotoConfig;
import net.spy.photo.PhotoSecurity;
import net.spy.photo.PhotoSessionData;
import net.spy.photo.User;
import net.spy.photo.UserFactory;
import net.spy.photo.log.PhotoLogEntry;
import net.spy.util.PwGen;

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

	private void persist(User user,
		HttpServletRequest request,
		HttpServletResponse response) throws Exception {

		// Get a persistent session ID
		String persess=PwGen.getPass(16);

		// Add a cookie
		Cookie c=new Cookie(PERSESS_COOKIE, persess);
		// Let's keep it for about two months.
		c.setMaxAge(86400*30*2);
		response.addCookie(c);

		UserFactory uf=UserFactory.getInstance();

		MutableUser muser=uf.getMutable(user.getId());
		// Set the ID
		muser.setPersess(persess);

		uf.persist(muser);
	}

	/**
	 * Process the request.
	 */
	public ActionForward spyExecute(ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,HttpServletResponse response)
		throws Exception {

		DynaActionForm lf=(DynaActionForm)form;

		PhotoSessionData sessionData=getSessionData(request);

		User user=Persistent.getSecurity().getUser(
			(String)lf.get("username"));

		PhotoSecurity sec=Persistent.getSecurity();
		if(sec.checkPassword((String)lf.get("password"), user.getPassword())) {
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

		ActionForward rv=null;
		String r=request.getParameter("return");
		if(r != null) {
			String next=r;
			if(r.equals("refer")) {
				next=request.getHeader("Referer");
			}
			response.sendRedirect(next);
		} else {
			rv=mapping.findForward("next");
		}

		// Go ahead and say it's alright.
		return(rv);
	}

}

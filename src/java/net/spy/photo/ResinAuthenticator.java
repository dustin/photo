// Copyright (c) 2005  Dustin Sallings <dustin@spy.net>
// arch-tag: 03657D4B-35CB-4D65-92D2-9E31A7AE1EC4

package net.spy.photo;

import java.security.Principal;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.caucho.server.security.AbstractAuthenticator;

/**
 * Resin-specific authenticator.
 */
public class ResinAuthenticator extends AbstractAuthenticator {

	// This looks much like the one from jwebkit, but uses photo objects since
	// photo isn't using jwebkit.UserFactory.
	protected Principal loginImpl(HttpServletRequest req,
		HttpServletResponse res, ServletContext context,
		String username, String password) throws ServletException {

		User rv=null;

		try {
			UserFactory uf=UserFactory.getInstance();
			User tmp=uf.getUser(username);
			PhotoSecurity sec=Persistent.getSecurity();
			if(sec.checkPassword(password, tmp.getPassword())) {
				rv=tmp;
			}
			context.log("Logged in " + rv);
		} catch(Exception e) {
			context.log("Problem logging in user", e);
		}

		if(rv != null) {
			hasAuthenticated(req, res, context, rv);
		}
		return(rv);
	}

	/** 
	 * Set the user in the sessionData after authenticating.
	 */
	protected void hasAuthenticated(HttpServletRequest req,
		HttpServletResponse res, ServletContext context, User user) {

		HttpSession session=req.getSession(true);
		PhotoSessionData psd=(PhotoSessionData)session.getAttribute(
			PhotoSessionData.SES_ATTR);
		assert psd != null : "no PhotoSessionData";
		psd.setUser(user);
	}

	public boolean isUserInRole(HttpServletRequest req,
		HttpServletResponse res, ServletContext context,
		Principal user, String role) {

		User u=(User)user;
		boolean rv=u.isInRole(role);
		return(rv);
	}

}

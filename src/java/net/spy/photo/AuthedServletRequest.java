// Copyright (c) 2004  Dustin Sallings <dustin@spy.net>

package net.spy.photo;

import java.security.Principal;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * Servlet request adaptor to provide standard authentication information.
 */
public class AuthedServletRequest extends HttpServletRequestWrapper {

	private User user=null;

	/**
	 * Get an instance of AuthedServletRequest.
	 */
	public AuthedServletRequest(HttpServletRequest req, User u) {
		super(req);
		this.user=u;
	}

	/** 
	 * Get the user.
	 */
	@Override
	public Principal getUserPrincipal() {
		return(user);
	}

	/** 
	 * Return true if the user is in the given role.
	 */
	@Override
	public boolean isUserInRole(String role) {
		boolean rv=false;
		if(user != null) {
			rv=user.isInRole(role);
		}
		return(rv);
	}

}

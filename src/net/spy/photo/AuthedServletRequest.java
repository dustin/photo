// Copyright (c) 2004  Dustin Sallings <dustin@spy.net>
// arch-tag: C09B3A6B-5D6C-11D9-A5D8-000A957659CC

package net.spy.photo;

import java.security.Principal;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * Servlet request adaptor to provide standard authentication information.
 */
public class AuthedServletRequest extends HttpServletRequestWrapper {

	private PhotoUser user=null;

	/**
	 * Get an instance of AuthedServletRequest.
	 */
	public AuthedServletRequest(HttpServletRequest req, PhotoUser user) {
		super(req);
		this.user=user;
	}

	/** 
	 * Get the user.
	 */
	public Principal getUserPrincipal() {
		return(user);
	}

	/** 
	 * Return true if the user is in the given role.
	 */
	public boolean isUserInRole(String role) {
		boolean rv=false;
		if(user != null) {
			rv=user.isInRole(role);
		}
		return(rv);
	}

}

// Copyright (c) 2004  Dustin Sallings <dustin@spy.net>
// arch-tag: BF1E17B8-5D6C-11D9-8569-000A957659CC

package net.spy.photo;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import net.spy.SpyObject;
import net.spy.photo.struts.LoginAction;

/**
 * Servlet filter for managing persistent auth.
 */
public class AuthFilter extends SpyObject implements Filter {

	/** 
	 * Perform initialization.
	 */
	public void init(FilterConfig config) throws ServletException {
		// Don't need it
	}

	/** 
	 * Destroy the filter.
	 */
	public void destroy() {
		// Don't need it
	}

	/** 
	 * Persistent login support.  If the session is new, check for a persess.
	 */
	public void doFilter(ServletRequest request, ServletResponse response,
		FilterChain chain) throws IOException, ServletException {

		HttpServletRequest hreq=(HttpServletRequest)request;
		HttpSession session=hreq.getSession(true);
		PhotoSessionData psd=(PhotoSessionData)session.getAttribute(
								PhotoSessionData.SES_ATTR);
		boolean isguest=true;
		if(psd != null) {
			User pu=psd.getUser();
			if(pu.isInRole(User.AUTHENTICATED)) {
				isguest=false;
			}
		}

		// Do the check whenever we're logged in as guest.
		if(session.isNew() || isguest) {
			Cookie cookies[]=hreq.getCookies();
			for(int i=0; cookies!=null && i<cookies.length; i++) {
				if(LoginAction.PERSESS_COOKIE.equals(cookies[i].getName())) {
					try {
						UserFactory uf=UserFactory.getInstance();
						User pu=uf.getUserByPersess(cookies[i].getValue());
						psd.setUser(pu);
						getLogger().info("Logged in user via persess: " + pu);
					} catch(PhotoUserException e) {
						getLogger().warn("Invalid persess", e);
					}
				}
			}
		}

		// The wrapped request providing standard looking auth stuff
		ServletRequest wrappedRequest=hreq;
		if(hreq.getUserPrincipal() == null) {
			wrappedRequest=new AuthedServletRequest(hreq, psd.getUser());
		}

		// Moving right along.
		chain.doFilter(wrappedRequest, response);

		// Don't allow guest sessions to hang around longer than necessary.
		if(isguest && psd.getImagesSeen() == 0) {
			session.invalidate();
		}
	}

}
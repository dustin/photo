// Copyright (c) 2004  Dustin Sallings <dustin@spy.net>

package net.spy.photo;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Cookie;

import net.spy.SpyObject;

/**
 * Servlet filter for managing persistent auth.
 */
public class AuthFilter extends SpyObject implements Filter {

	private FilterConfig conf=null;

	/**
	 * Get an instance of AuthFilter.
	 */
	public AuthFilter() {
		super();
	}

	/** 
	 * Perform initialization.
	 */
	public void init(FilterConfig config) throws ServletException {
		conf=config;
	}

	/** 
	 * Destroy the filter.
	 */
	public void destroy() {
		conf=null;
	}

	/** 
	 * Persistent login support.  If the session is new, check for a persess.
	 */
	public void doFilter(ServletRequest request, ServletResponse response,
		FilterChain chain) throws IOException, ServletException {

		HttpServletRequest hreq=(HttpServletRequest)request;
		HttpSession session=hreq.getSession(true);

		if(session.isNew()) {
			Cookie cookies[]=hreq.getCookies();
			for(int i=0; cookies!=null && i<cookies.length; i++) {
				if("persess".equals(cookies[i].getName())) {
					try {
						PhotoSessionData psd=
							(PhotoSessionData)session.getAttribute(
								PhotoSessionData.SES_ATTR);
						PhotoUser pu=PhotoUser.getPhotoUserByPersess(
							cookies[i].getValue());
						psd.setUser(pu);
						getLogger().info("Logged in user via persess: " + pu);
					} catch(PhotoUserException e) {
						getLogger().warn("Invalid persess", e);
					}
				}
			}
		}

		// Moving right along.
		chain.doFilter(request, response);
	}

}

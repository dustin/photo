// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: InitSessionData.java,v 1.4 2002/07/10 03:38:09 dustin Exp $

package net.spy.photo.taglib;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import net.spy.photo.Persistent;
import net.spy.photo.PhotoConfig;
import net.spy.photo.PhotoDimensions;
import net.spy.photo.PhotoDimensionsImpl;
import net.spy.photo.PhotoSessionData;

/**
 * Taglib that makes sure the session data is initialized appropriately.
 */
public class InitSessionData extends PhotoTag {

	/**
	 * Get an InitSessionData instance.
	 */
	public InitSessionData() {
		super();
	}

	/**
	 * Make sure the session data is initialized and available and all
	 * that.
	 */
	public int doStartTag() throws JspException {
	
		HttpSession session=pageContext.getSession();
		PhotoSessionData sessionData=
			(PhotoSessionData)session.getAttribute("photoSession");

		// If there's no session data, make it
		if(sessionData==null) {
			// Get the object
			sessionData=new PhotoSessionData();
			// Initialize the user
			sessionData.setUser(Persistent.getSecurity().getUser("guest"));

			// Initialize the optimal dimensions
			// Start with cookies
			HttpServletRequest req=(HttpServletRequest)pageContext.getRequest();
			Cookie cookies[] = req.getCookies();
			String dimss=null;
			if(cookies!=null) {
				for(int ci=0; ci<cookies.length && dimss == null; ci++) {
					String s = cookies[ci].getName();
					if(s.equalsIgnoreCase("photo_dims")) {
						dimss=cookies[ci].getValue();
					}
				}
			}
			// Figure out whether to get the dimensions from the cookie or
			// the config.
			PhotoDimensions dim=null;
			if(dimss==null) {
				PhotoConfig conf=new PhotoConfig();
				dim=new PhotoDimensionsImpl(
					conf.get("optimal_image_size", "800x600"));
			} else {
				dim=new PhotoDimensionsImpl(dimss);
				pageContext.getServletContext().log(
					"Loading dims from cookies:  " + dim);
			}

			// Stick it in the session
			sessionData.setOptimalDimensions(dim);
			session.setAttribute("photoSession", sessionData);
		}

		pageContext.setAttribute("sessionData", sessionData,
			PageContext.REQUEST_SCOPE);

		return EVAL_BODY_INCLUDE;
	}
}

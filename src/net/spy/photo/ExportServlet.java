// Copyright (c) 2004  Dustin Sallings <dustin@spy.net>

package net.spy.photo;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.spy.jwebkit.JWHttpServlet;

import net.spy.photo.struts.SearchForm;

/**
 * Export data from the photo album.
 */
public class ExportServlet extends JWHttpServlet {

	/**
	 * Get an instance of ExportServlet.
	 */
	public ExportServlet() {
		super();
	}

	/** 
	 * Process the request.
	 */
	protected void doGetOrPost(HttpServletRequest req, HttpServletResponse res)
		throws ServletException, IOException {

		try {
			HttpSession ses=req.getSession(false);
			PhotoSessionData sessionData=(PhotoSessionData)ses.getAttribute(
				PhotoSessionData.SES_ATTR);

			// If we want to construct this for a different user, specify the
			// username here.
			if(req.getParameter("user") != null) {
				PhotoUser thisUser=sessionData.getUser();
				if(!thisUser.isInRole("admin")) {
					throw new ServletException("You are not an admin");
				}
				String spec=req.getParameter("user");
				PhotoUser thatUser=Persistent.getSecurity().getUser(spec);
				PhotoSessionData newSess=new PhotoSessionData();
				newSess.setUser(thatUser);
				newSess.setOptimalDimensions(
					sessionData.getOptimalDimensions());
				sessionData=newSess;
			}

			SearchForm sf=new SearchForm();
			sf.setSdirection("desc");
			PhotoSearch ps=PhotoSearch.getInstance();
			PhotoSearchResults psr=ps.performSearch(sf, sessionData);

			res.setContentType("text/xml");
			ServletOutputStream sos=res.getOutputStream();

			Search2XML s2x=Search2XML.getInstance();
			s2x.stream(psr, sos);
		} catch(Exception e) {
			throw new ServletException("Problem exporting data", e);
		}
	}

}

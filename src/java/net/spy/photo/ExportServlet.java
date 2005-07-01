// Copyright (c) 2004  Dustin Sallings <dustin@spy.net>
// arch-tag: CFF3FF42-5D6C-11D9-8B1E-000A957659CC

package net.spy.photo;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.spy.jwebkit.JWHttpServlet;
import net.spy.photo.search.Search;
import net.spy.photo.search.Search2XML;
import net.spy.photo.search.SearchResults;
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

			SearchForm sf=new SearchForm();
			sf.setSdirection("desc");
			Search ps=Search.getInstance();
			SearchResults psr=ps.performSearch(sf, sessionData);

			res.setContentType("text/xml");
			ServletOutputStream sos=res.getOutputStream();

			Search2XML s2x=Search2XML.getInstance();
			s2x.stream(psr, sos);
		} catch(Exception e) {
			throw new ServletException("Problem exporting data", e);
		}
	}

}
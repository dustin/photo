// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: SaveSearchAction.java,v 1.1 2002/05/23 06:54:51 dustin Exp $

package net.spy.photo.struts;

import java.io.IOException;
import java.sql.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.apache.struts.action.*;

import net.spy.*;

import net.spy.photo.*;

/**
 * Save a search.
 */
public class SaveSearchAction extends PhotoAction {

	/**
	 * Get an instance of SaveSearchAction.
	 */
	public SaveSearchAction() {
		super();
	}

	/**
	 * Process the save search.
	 */
	public ActionForward perform(ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,HttpServletResponse response)
		throws IOException, ServletException {

		// Cast the form
		SaveSearchForm ssf=(SaveSearchForm)form;

		// Get the session data.
		PhotoSessionData sessionData=getSessionData(request);

		// Get the PhotoSearch object which manages the search save.
		try {
			PhotoSearch search=new PhotoSearch();
			search.saveSearch(ssf, sessionData.getUser());
		} catch(Exception e) {
			throw new ServletException("Error saving search", e);
		}

		// If we made it this far, we were successful.
		return(mapping.findForward("success"));
	}

}

// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: SaveSearchAction.java,v 1.3 2003/05/25 08:17:42 dustin Exp $

package net.spy.photo.struts;

import java.io.IOException;

import javax.servlet.ServletException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.spy.photo.PhotoSearch;
import net.spy.photo.PhotoSessionData;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

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
	public ActionForward execute(ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,HttpServletResponse response)
		throws Exception {

		// Cast the form
		SaveSearchForm ssf=(SaveSearchForm)form;

		// Get the session data.
		PhotoSessionData sessionData=getSessionData(request);

		// Get the PhotoSearch object which manages the search save.
		PhotoSearch search=new PhotoSearch();
		search.saveSearch(ssf, sessionData.getUser());

		// If we made it this far, we were successful.
		return(mapping.findForward("success"));
	}

}

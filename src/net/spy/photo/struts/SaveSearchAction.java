// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: SaveSearchAction.java,v 1.6 2003/07/23 04:29:26 dustin Exp $

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
import org.apache.struts.action.DynaActionForm;

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
	public ActionForward spyExecute(ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,HttpServletResponse response)
		throws Exception {

		// Cast the form
		DynaActionForm ssf=(DynaActionForm)form;

		// Get the session data.
		PhotoSessionData sessionData=getSessionData(request);

		// Get the PhotoSearch object which manages the search save.
		PhotoSearch search=PhotoSearch.getInstance();
		search.saveSearch(
			(String)ssf.get("name"),
			(String)ssf.get("search"),
			sessionData.getUser());

		// If we made it this far, we were successful.
		return(mapping.findForward("next"));
	}

}

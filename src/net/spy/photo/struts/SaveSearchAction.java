// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
// arch-tag: 369F5F80-5D6E-11D9-9068-000A957659CC

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

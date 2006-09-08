// Copyright (c) 2003  Dustin Sallings <dustin@spy.net>
// arch-tag: 28161F5E-5D6E-11D9-ABA3-000A957659CC

package net.spy.photo.struts;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.spy.photo.search.SavedSearch;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.DynaActionForm;

/**
 * Load a saved search and overwrite the search form with that search.
 */
public class GetSavedSearchAction extends PhotoAction {

	/**
	 * Load a saved search from a dynaform that has <q>search_id</q>
	 * defined.
	 */
	public ActionForward spyExecute(ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,HttpServletResponse response)
		throws Exception {

		DynaActionForm df=(DynaActionForm)form;

		// Get the search ID
		int id=((Integer)df.get("searchId")).intValue();

		// Get the search for that ID
		SavedSearch ss=SavedSearch.getSearch(id);

		// Create a SearchForm out of that thing
		SearchForm sf=ss.getSearchForm();

		// Grab the session and put the search form in it
		HttpSession session=request.getSession();
		session.setAttribute("searchForm", sf);

		return(mapping.findForward("next"));
	}

}

// Copyright (c) 2003  Dustin Sallings <dustin@spy.net>
// arch-tag: 29CC6236-5D6E-11D9-9B65-000A957659CC

package net.spy.photo.struts;

import java.io.IOException;

import java.util.Map;

import javax.servlet.ServletException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.beanutils.PropertyUtils;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.DynaActionForm;

import net.spy.photo.SavedSearch;

/**
 * Load a saved search and overwrite the search form with that search.
 */
public class GetSavedSearchesAction extends PhotoAction {

	/**
	 * Get an instance of GetSavedSearchesAction.
	 */
	public GetSavedSearchesAction() {
		super();
	}

	/**
	 * Load a saved search from a dynaform that has <q>search_id</q>
	 * defined.
	 */
	public ActionForward spyExecute(ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,HttpServletResponse response)
		throws Exception {

		request.setAttribute("searches", SavedSearch.getSearches());

		return(mapping.findForward("next"));
	}

}

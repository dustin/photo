// Copyright (c) 2003  Dustin Sallings <dustin@spy.net>
//
// $Id: GetSavedSearchAction.java,v 1.2 2003/08/09 20:48:19 dustin Exp $

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

import net.spy.jwebkit.RequestUtil;

import net.spy.photo.SavedSearch;

/**
 * Load a saved search and overwrite the search form with that search.
 */
public class GetSavedSearchAction extends PhotoAction {

	private static final String CHARSET="UTF-8";

	/**
	 * Get an instance of GetSavedSearchAction.
	 */
	public GetSavedSearchAction() {
		super();
	}

	private SearchForm getSearchForm(SavedSearch ss) throws Exception {
		SearchForm sf=new SearchForm();
		Map m=RequestUtil.parseQueryString(ss.getSearch(), CHARSET);

		// Stuff that should be copied directly.
		String straightCopy[]={
			"fieldjoin", "field", "keyjoin", "what", "tstartjoin", "what",
			"tstartjoin", "tstart", "tendjoin", "tend", "startjoin",
			"start", "endjoin", "end", "order", "sdirection", "maxret",
			"filter", "action"
		};
		PropertyUtils pu=new PropertyUtils();
		for(int i=0; i<straightCopy.length; i++) {
			String vs[]=(String[])m.get(straightCopy[i]);
			if(vs != null) {
				String v=vs[0];
				pu.setProperty(sf, straightCopy[i], v);
				/*
				getLogger().info("Set " + straightCopy[i] + " to " + v);
				*/
			}
		}
		String vs[]=(String[])m.get("cats");
		if(vs != null) {
			pu.setProperty(sf, "cats", vs);
			/*
			getLogger().info("Set cats to " + vs);
			*/
		}

		return(sf);
	}

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
		SearchForm sf=getSearchForm(ss);
		
		// Grab the session and put the search form in it
		HttpSession session=request.getSession();
		session.setAttribute("searchForm", sf);

		/*
		PropertyUtils pu=new PropertyUtils();
		getLogger().info("Search form:  " + pu.describe(sf));
		*/

		return(mapping.findForward("next"));
	}

}

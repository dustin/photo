// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>

package net.spy.photo.struts;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import net.spy.photo.PhotoSessionData;
import net.spy.photo.search.ParallelSearch;
import net.spy.photo.search.Search;
import net.spy.photo.search.SearchResults;

/**
 * Perform a search.
 */
public class SearchAction extends PhotoAction {

	/**
	 * Perform the action.
	 */
	@Override
	public ActionForward spyExecute(ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,HttpServletResponse response)
		throws Exception {

		PhotoSessionData sessionData=getSessionData(request);

		SearchForm sf=(SearchForm)form;

		// Perform the search
		ParallelSearch ps=ParallelSearch.getInstance();
		SearchResults results=null;
		results=ps.performSearch(sf, sessionData.getUser(),
			sessionData.getOptimalDimensions());
		sessionData.setResults(results);
		request.setAttribute("search_results", results);
		sessionData.setEncodedSearch(Search.getInstance().encodeSearch(sf));

		String f=sf.getAction();
		if(f==null) {
			f="next";
		}

		// next
		return(mapping.findForward(f));
	}

}

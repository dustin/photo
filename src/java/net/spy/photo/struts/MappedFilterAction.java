// Copyright (c) 2004  Dustin Sallings <dustin@spy.net>

package net.spy.photo.struts;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.spy.photo.PhotoSessionData;
import net.spy.photo.filter.Filter;
import net.spy.photo.search.SearchResults;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * Perform the mapped filter.
 */
public class MappedFilterAction extends PhotoAction {

	/**
	 * Perform the mapped in filter.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public ActionForward spyExecute(ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,HttpServletResponse response)
		throws Exception {

		FilterMapping fm=(FilterMapping)mapping;

		// Instantiate the filter
		Class<Filter> c=(Class<Filter>) Class.forName(fm.getFilterClass());
		Filter f=c.newInstance();

		// Get the search results
		PhotoSessionData sessionData=getSessionData(request);
		SearchResults results=sessionData.getResults();
		sessionData.setResults(f.filter(results));

		return(mapping.findForward("next"));
	}

}

// Copyright (c) 2004  Dustin Sallings <dustin@spy.net>

package net.spy.photo.struts;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import net.spy.photo.PhotoSessionData;
import net.spy.photo.PhotoSearchResults;
import net.spy.photo.filter.Filter;

/**
 * Perform the mapped filter.
 */
public class MappedFilterAction extends PhotoAction {

	/**
	 * Get an instance of MappedFilterAction.
	 */
	public MappedFilterAction() {
		super();
	}

	/**
	 * Perform the mapped in filter.
	 */
	public ActionForward spyExecute(ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,HttpServletResponse response)
		throws Exception {

		FilterMapping fm=(FilterMapping)mapping;

		// Instantiate the filter
		Class c=Class.forName(fm.getFilterClass());
		Filter f=(Filter)c.newInstance();

		// Get the search results
		PhotoSessionData sessionData=getSessionData(request);
		PhotoSearchResults results=sessionData.getResults();
		sessionData.setResults(f.filter(results));

		return(mapping.findForward("next"));
	}

}
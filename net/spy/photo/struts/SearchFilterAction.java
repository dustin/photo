// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: SearchFilterAction.java,v 1.2 2002/06/29 07:40:07 dustin Exp $

package net.spy.photo.struts;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.*;

import net.spy.photo.*;
import net.spy.photo.filter.*;

/**
 * Filter search results.
 */
public class SearchFilterAction extends PhotoAction {

	/**
	 * Get an instance of SearchFilterAction.
	 */
	public SearchFilterAction() {
		super();
	}

	/**
	 * Perform the action.
	 */
	public ActionForward perform(ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,HttpServletResponse response)
		throws IOException, ServletException {

		// Get the form
		SearchForm sf=(SearchForm)form;

		String filterName=sf.getFilter();

		// Don't do anything if there's not a filter.
		if(filterName!=null && !filterName.equals("")) {
			// Get the search results
			PhotoSessionData sessionData=getSessionData(request);
			PhotoSearchResults results=sessionData.getResults();
			if(results==null) {
				throw new ServletException("No search results found!");
			}

			Filter filter=null;

			// Figure out what filter to use
			if(filterName.equals("onceamonth")) {
				OnceAMonthFilter oam=new OnceAMonthFilter();
				// Save the sort direction.
				if(sf.getSdirection().equals("desc")) {
					oam.setSortDirection(OnceAMonthFilter.SORT_REVERSE);
				} else {
					oam.setSortDirection(OnceAMonthFilter.SORT_FORWARD);
				}
				filter=oam;
			} else {
				throw new ServletException("Unknown filter:  " + filterName);
			}

			// perform the filtration and set the results.
			try {
				results=filter.filter(results);
			} catch(PhotoException pe) {
				throw new ServletException("Error filtering results", pe);
			}
			sessionData.setResults(results);
		}

		// next
		return(mapping.findForward("success"));
	}

}

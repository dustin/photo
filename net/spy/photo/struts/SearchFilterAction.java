// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: SearchFilterAction.java,v 1.9 2003/07/23 04:29:26 dustin Exp $

package net.spy.photo.struts;

import java.io.IOException;

import javax.servlet.ServletException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.spy.photo.PhotoException;
import net.spy.photo.PhotoSearchResults;
import net.spy.photo.PhotoSessionData;

import net.spy.photo.filter.Filter;
import net.spy.photo.filter.SortingFilter;
import net.spy.photo.filter.OnceAMonthFilter;
import net.spy.photo.filter.OnceAWeekFilter;
import net.spy.photo.filter.OnceADayFilter;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

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
	public ActionForward spyExecute(ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,HttpServletResponse response)
		throws Exception {

		// Get the form
		SearchForm sf=(SearchForm)form;

		String filterName=sf.getFilter();

		getLogger().info("Filter called with " + filterName);

		// Don't do anything if there's not a filter.
		if(filterName!=null && !filterName.equals("")) {
			// Get the search results
			PhotoSessionData sessionData=getSessionData(request);
			PhotoSearchResults results=sessionData.getResults();
			getLogger().info("Filtering results:  " + results);
			if(results==null) {
				throw new ServletException("No search results found!");
			}

			Filter filter=null;

			// Figure out what filter to use
			if(filterName.equals("onceamonth")) {
				filter=new OnceAMonthFilter();
			} else if(filterName.equals("onceaweek")) {
				filter=new OnceAWeekFilter();
			} else if(filterName.equals("onceaday")) {
				filter=new OnceADayFilter();
			} else {
				throw new ServletException("Unknown filter:  " + filterName);
			}

			// Filters that deal with sorting
			if(filter instanceof SortingFilter) {
				SortingFilter sfil=(SortingFilter)filter;
				// Save the sort direction.
				if(sf.getSdirection().equals("desc")) {
					sfil.setSortDirection(SortingFilter.SORT_REVERSE);
				} else {
					sfil.setSortDirection(SortingFilter.SORT_FORWARD);
				}
			}

			// perform the filtration and set the results.
			results=filter.filter(results);
			getLogger().info("Filtered results to:  " + results
				+ " with " + filter);
			sessionData.setResults(results);
		}

		String f=sf.getAction();
		if(f==null) {
			f="next";
		}

		// next
		return(mapping.findForward(f));
	}

}

// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: SearchFilterAction.java,v 1.9 2003/07/23 04:29:26 dustin Exp $

package net.spy.photo.struts;

import java.util.Map;
import java.util.HashMap;

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

	private Map filters=null;

	/**
	 * Get an instance of SearchFilterAction.
	 */
	public SearchFilterAction() {
		super();
		initFilters();
	}

	private void initFilters() {
		filters=new HashMap();
		filters.put("onceamonth", new OnceAMonthFilter());
		filters.put("onceaweek", new OnceAWeekFilter());
		filters.put("onceaday", new OnceADayFilter());
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

		// Don't do anything if there's not a filter.
		if(filterName!=null && !filterName.equals("")) {
			getLogger().debug("Filter called with " + filterName);
			// Get the search results
			PhotoSessionData sessionData=getSessionData(request);
			PhotoSearchResults results=sessionData.getResults();
			if(getLogger().isDebugEnabled()) {
				getLogger().debug("Filtering results:  " + results);
			}
			if(results==null) {
				throw new ServletException("No search results found!");
			}

			// Get the filter
			Filter filter=(Filter)filters.get(filterName);
			if(filter == null) {
				throw new ServletException("Unknown filter:  " + filterName);
			}

			// If the filter deals with sorting, include the sort direction.
			if(filter instanceof SortingFilter) {
				SortingFilter sfil=(SortingFilter)filter;
				// Get the sorting direction
				int dir=SortingFilter.SORT_FORWARD;
				if("desc".equals(sf.getSdirection())) {
					dir=SortingFilter.SORT_REVERSE;
				}
				results=sfil.filter(results, dir);
			} else {
				results=filter.filter(results);
			}

			if(getLogger().isDebugEnabled()) {
				getLogger().debug("Filtered results to:  " + results
					+ " with " + filter);
			}
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

// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: SearchAction.java,v 1.9 2003/07/23 04:29:26 dustin Exp $

package net.spy.photo.struts;

import java.io.IOException;

import javax.servlet.ServletException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.spy.photo.PhotoSearch;
import net.spy.photo.PhotoSearchResults;
import net.spy.photo.PhotoSessionData;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * Perform a search.
 */
public class SearchAction extends PhotoAction {

	/**
	 * Get an instance of SearchAction.
	 */
	public SearchAction() {
		super();
	}

	/**
	 * Perform the action.
	 */
	public ActionForward spyExecute(ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,HttpServletResponse response)
		throws Exception {

		PhotoSessionData sessionData=getSessionData(request);

		SearchForm sf=(SearchForm)form;

		// Perform the search
		PhotoSearch ps=new PhotoSearch();
		PhotoSearchResults results=null;
		results=ps.performSearch(sf, sessionData);
		sessionData.setResults(results);
		sessionData.setEncodedSearch(ps.encodeSearch(sf));

		String f=sf.getAction();
		if(f==null) {
			f="next";
		}

		// next
		return(mapping.findForward(f));
	}

}
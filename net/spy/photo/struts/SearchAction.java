// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: SearchAction.java,v 1.5 2002/07/10 03:38:09 dustin Exp $

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
	public ActionForward perform(ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,HttpServletResponse response)
		throws IOException, ServletException {

		PhotoSessionData sessionData=getSessionData(request);

		// Perform the search
		PhotoSearch ps=new PhotoSearch();
		PhotoSearchResults results=null;
		results=ps.performSearch((SearchForm)form, sessionData);
		sessionData.setResults(results);
		sessionData.setEncodedSearch(ps.encodeSearch((SearchForm)form));

		ActionForward rv=mapping.findForward("success");

		return(rv);
	}

}

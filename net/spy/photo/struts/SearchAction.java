// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: SearchAction.java,v 1.3 2002/05/18 07:52:07 dustin Exp $

package net.spy.photo.struts;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.apache.struts.action.*;

import net.spy.photo.*;

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
		PhotoSearch2 ps=new PhotoSearch2();
		PhotoSearchResults results=null;
		results=ps.performSearch((SearchForm)form, sessionData);
		sessionData.setResults(results);
		sessionData.setEncodedSearch(ps.encodeSearch((SearchForm)form));

		ActionForward rv=mapping.findForward("success");

		return(rv);
	}

}

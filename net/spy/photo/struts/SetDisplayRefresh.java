// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: SetDisplayRefresh.java,v 1.4 2003/07/23 04:29:26 dustin Exp $

package net.spy.photo.struts;

import java.io.IOException;

import javax.servlet.ServletException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import net.spy.photo.PhotoSessionData;
import net.spy.photo.PhotoSearchResults;

import net.spy.photo.PhotoUtil;

/**
 * If requested, set up a display refresh for the display page.
 */
public class SetDisplayRefresh extends PhotoAction {

	/**
	 * Get an instance of SetDisplayRefresh.
	 */
	public SetDisplayRefresh() {
		super();
	}

	/**
	 * Set the optimal viewing size.
	 */
	public ActionForward spyExecute(ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,HttpServletResponse response)
		throws Exception {

		// Get the session data
		PhotoSessionData sessionData=getSessionData(request);
		PhotoSearchResults results=sessionData.getResults();
		if(results==null) {
			throw new ServletException("No results in session.");
		}

		// Check for a search ID
		String searchId=request.getParameter("search_id");
		if(searchId != null) {
			int sid=Integer.parseInt(searchId);
			if( (sid+1) < results.size()) {
				String loc="/refreshDisplay.do?search_id=" + (sid+1);
				String aLoc=PhotoUtil.getRelativeUri(request, loc);
				getLogger().info("Refreshing to:  " + aLoc);
				response.addHeader("Refresh", "5; URL=" + aLoc);
			}
		}

		return(mapping.findForward("next"));
	}

}

// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: SetDisplayRefresh.java,v 1.1 2003/04/25 06:32:23 dustin Exp $

package net.spy.photo.struts;

import java.io.IOException;

import javax.servlet.ServletException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import net.spy.photo.RefreshBean;
import net.spy.photo.PhotoSessionData;
import net.spy.photo.PhotoSearchResults;

import net.spy.photo.taglib.RefreshTag;

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
	public ActionForward perform(ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,HttpServletResponse response)
		throws IOException, ServletException {

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
				RefreshBean rb=new RefreshBean();
				rb.setDelay(5);
				rb.setLocation("/refreshDisplay.do?search_id=" + (sid+1));
				log("Setting refresh bean:  " + rb);
				request.setAttribute(RefreshTag.REFRESH_BEAN, rb);
			}
		}

		return(mapping.findForward("success"));
	}

}

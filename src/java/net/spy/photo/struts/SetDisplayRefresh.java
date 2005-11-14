// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
// arch-tag: 3BB7614A-5D6E-11D9-A1CF-000A957659CC

package net.spy.photo.struts;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.spy.photo.PhotoSessionData;
import net.spy.photo.PhotoUtil;
import net.spy.photo.search.SearchResults;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * If requested, set up a display refresh for the display page.
 */
public class SetDisplayRefresh extends PhotoAction {

	/** 
	 * Request attribute set when slideshow mode is activated.
	 */
	public static final String SLIDESHOW_MODE="slideshowMode";

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
		SearchResults results=sessionData.getResults();
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

				request.setAttribute(SLIDESHOW_MODE, "1");
			}
		}

		return(mapping.findForward("next"));
	}

}

// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
// arch-tag: 3BB7614A-5D6E-11D9-A1CF-000A957659CC

package net.spy.photo.struts;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import net.spy.photo.PhotoSessionData;
import net.spy.photo.PhotoUtil;
import net.spy.photo.search.SearchResults;

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

		int id=-1;
		String idS=request.getParameter("id");
		if(idS == null) {
			String relPos=request.getParameter("relativeTo");
			if(relPos != null) {
				id=sessionData.getResultIdByPosition(Integer.parseInt(relPos));
			}
		} else {
			id=Integer.parseInt(idS);
		}

		int searchPos=sessionData.getResultPos(id);
		if(searchPos != -1) {
			int next=sessionData.getResultIdByPosition(searchPos + 1);
			if(next != -1) {
				String loc="/refreshDisplay.do?id=" + next;
				String aLoc=PhotoUtil.getRelativeUri(request, loc);
				getLogger().info("Refreshing to:  " + aLoc);
				response.addHeader("Refresh", "5; URL=" + aLoc);

				request.setAttribute(SLIDESHOW_MODE, "1");
			}
		}

		ActionForward af=new ActionForward(mapping.findForward("next"));
		af.setPath(af.getPath() + "?id=" + id);
		return(af);
	}

}

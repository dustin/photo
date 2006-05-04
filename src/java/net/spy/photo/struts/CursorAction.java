// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
// arch-tag: 1A8AC647-5D6E-11D9-A9E5-000A957659CC

package net.spy.photo.struts;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.spy.photo.Cursor;
import net.spy.photo.PhotoSessionData;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.DynaActionForm;

/**
 * Action to adjust a cursor before sending it to a result page.
 */
public class CursorAction extends PhotoAction {

	/**
	 * Get an instance of CursorAction.
	 */
	public CursorAction() {
		super();
	}

	/**
	 * Perform the adjustment (if any).
	 */
	public ActionForward spyExecute(ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,HttpServletResponse response)
		throws Exception {

		DynaActionForm cf=(DynaActionForm)form;

		// Verify there's something to do.
		Integer offset=(Integer)cf.get("startOffset");
		String whichCursor=(String)cf.get("whichCursor");
		if(offset!=null && whichCursor!=null) {
			// Get the session data
			PhotoSessionData sessionData=getSessionData(request);
			// Get the cursoor
			Cursor c=null;
			if(whichCursor.equals("results")) {
				c=sessionData.getResults();
			} else if(whichCursor.equals("comments")) {
				c=sessionData.getComments();
			} else {
				throw new ServletException(
					"Invalid cursor, how did this get through?");
			}

			// And set the offset.
			c.setPageNumber(offset.intValue());
			getLogger().debug("CursorAction sought to " + offset
				+ ", yielding " + c);
		}

		return(mapping.findForward("next"));
	}

}

// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: CursorAction.java,v 1.6 2003/05/25 08:17:41 dustin Exp $

package net.spy.photo.struts;

import java.io.IOException;

import javax.servlet.ServletException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.spy.photo.Cursor;
import net.spy.photo.PhotoSessionData;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

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
	public ActionForward execute(ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,HttpServletResponse response)
		throws Exception {

		CursorForm cf=(CursorForm)form;

		// Verify there's something to do.
		if(cf!=null && cf.getStartOffset()!=null && cf.getWhichCursor()!=null) {
			// Get the session data
			PhotoSessionData sessionData=getSessionData(request);
			// Get the cursoor
			Cursor c=null;
			if(cf.getWhichCursor().equals("results")) {
				c=sessionData.getResults();
			} else if(cf.getWhichCursor().equals("comments")) {
				c=sessionData.getComments();
			} else {
				throw new ServletException(
					"Invalid cursor, how did this get through?");
			}

			// And set the offset.
			int offset=Integer.parseInt(cf.getStartOffset());
			c.set(offset);
			System.out.println("CursorAction sought to " + offset
				+ ", yielding " + c);
		}

		return(mapping.findForward("success"));
	}

}

// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: ListCommentsAction.java,v 1.1 2002/05/18 05:31:55 dustin Exp $

package net.spy.photo.struts;

import java.io.IOException;
import javax.servlet.*;
import javax.servlet.http.*;

import org.apache.struts.action.*;

import net.spy.photo.*;

/**
 * Initialize the comments list for this user.
 */
public class ListCommentsAction extends PhotoAction {

	/**
	 * Get an instance of ListCommentsAction.
	 */
	public ListCommentsAction() {
		super();
	}

	/**
	 * Load the comments cursor.
	 */
	public ActionForward perform(ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,HttpServletResponse response)
		throws IOException, ServletException {

		System.out.println("Preparing a new comments cursor.");

		PhotoSessionData sessionData=getSessionData(request);
		try {
			PhotoUser user=sessionData.getUser();
			Cursor comments=new Cursor(
				Comment.getAllComments(sessionData.getUser()));
			sessionData.setComments(comments);
		} catch(Exception e) {
			throw new ServletException("Error preparing comments", e);
		}

		return(mapping.findForward("success"));
	}

}

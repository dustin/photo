// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: ListCommentsAction.java,v 1.3 2002/07/10 03:38:09 dustin Exp $

package net.spy.photo.struts;

import java.io.IOException;

import javax.servlet.ServletException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.spy.photo.Comment;
import net.spy.photo.Cursor;
import net.spy.photo.PhotoSessionData;
import net.spy.photo.PhotoUser;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

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
			Cursor comments=new Cursor(Comment.getAllComments(user));
			sessionData.setComments(comments);
		} catch(Exception e) {
			throw new ServletException("Error preparing comments", e);
		}

		return(mapping.findForward("success"));
	}

}

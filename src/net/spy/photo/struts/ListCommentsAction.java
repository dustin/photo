// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
// arch-tag: 2B082E21-5D6E-11D9-BEBF-000A957659CC

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
	public ActionForward spyExecute(ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,HttpServletResponse response)
		throws Exception {

		getLogger().debug("Preparing a new comments cursor.");

		PhotoSessionData sessionData=getSessionData(request);
		PhotoUser user=sessionData.getUser();
		Cursor comments=new Cursor(Comment.getAllComments(user));
		sessionData.setComments(comments);

		return(mapping.findForward("next"));
	}

}

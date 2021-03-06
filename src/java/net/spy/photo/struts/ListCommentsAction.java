// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>

package net.spy.photo.struts;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.spy.photo.Comment;
import net.spy.photo.Cursor;
import net.spy.photo.GroupedComments;
import net.spy.photo.PhotoSessionData;
import net.spy.photo.User;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * Initialize the comments list for this user.
 */
public class ListCommentsAction extends PhotoAction {

	/**
	 * Load the comments cursor.
	 */
	@Override
	public ActionForward spyExecute(ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,HttpServletResponse response)
		throws Exception {

		getLogger().debug("Preparing a new comments cursor.");

		PhotoSessionData sessionData=getSessionData(request);
		User user=sessionData.getUser();
		Cursor<?> comments=new Cursor<GroupedComments>(
				Comment.getGroupedComments(user));
		sessionData.setComments(comments);

		return(mapping.findForward("next"));
	}

}

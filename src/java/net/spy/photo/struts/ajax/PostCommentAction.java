// Copyright (c) 2005  Dustin Sallings <dustin@spy.net>
// arch-tag: C604AB09-270D-404E-ADF4-66E2F2E88C7C

package net.spy.photo.struts.ajax;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import net.spy.jwebkit.SAXAble;
import net.spy.jwebkit.struts.AjaxAction;

import net.spy.db.Saver;
import net.spy.photo.Comment;
import net.spy.photo.Persistent;
import net.spy.photo.PhotoConfig;
import net.spy.photo.PhotoSessionData;
import net.spy.photo.User;

/**
 * Post a comment.
 */
public class PostCommentAction extends PhotoAjaxAction {

	protected SAXAble getResults(ActionForm form,
		HttpServletRequest request) throws Exception {
		String commentString=request.getParameter("comment");
		if(commentString == null || commentString.length()==0) {
			throw new NullPointerException("comment");
		}
		String idString=request.getParameter("imgId");
		if(idString == null) {
			throw new NullPointerException("imgId");
		}
		int id=Integer.parseInt(idString);

		PhotoSessionData ses=getSessionData(request);
		User user=ses.getUser();

		// Check the access
		Persistent.getSecurity().checkAccess(user, id);

		// Construct the comment.
		Comment comment=new Comment();
		comment.setUser(user);
		comment.setPhotoId(id);
		comment.setRemoteAddr(request.getRemoteAddr());
		comment.setNote(commentString);

		Saver s=new Saver(PhotoConfig.getInstance());
		s.save(comment);

		getLogger().info("Posting comment for image " + id + ":  " + comment);

		return(null);
	}

}

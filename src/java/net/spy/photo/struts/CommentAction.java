// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
// arch-tag: 19A4E39C-5D6E-11D9-9821-000A957659CC

package net.spy.photo.struts;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.spy.db.Saver;
import net.spy.photo.Comment;
import net.spy.photo.Persistent;
import net.spy.photo.PhotoConfig;
import net.spy.photo.PhotoSessionData;
import net.spy.photo.User;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.DynaActionForm;

/**
 * Action that submits a comment.
 */
public class CommentAction extends PhotoAction {

	/**
	 * Get an instance of CommentAction.
	 */
	public CommentAction() {
		super();
	}

	/**
	 * Process the comment thing.
	 */
	public ActionForward spyExecute(
		ActionMapping mapping, ActionForm form,
		HttpServletRequest request, HttpServletResponse response) 
		throws Exception {

		DynaActionForm df=(DynaActionForm)form;

		System.err.println("Processing comment:  " + df);

		// Get the session data
		PhotoSessionData sessionData=getSessionData(request);
		// Get the user
		User user=sessionData.getUser();

		Integer imageInteger=(Integer)df.get("imageId");
		int imageId=imageInteger.intValue();

		// Check permission
		try {
			Persistent.getSecurity().checkAccess(user, imageId);
		} catch(Exception e) {
			throw new ServletException(e.getMessage(), e);
		}

		// Construct the comment.
		Comment comment=new Comment();
		comment.setUser(user);
		comment.setPhotoId(imageId);
		comment.setRemoteAddr(request.getRemoteAddr());
		comment.setNote( (String)df.get("comment") );

		try {
			Saver s=new Saver(PhotoConfig.getInstance());
			s.save(comment);
		} catch(Exception e) {
			throw new ServletException("Error saving comment", e);
		}

		ActionForward rv=null;

		// Get the configured forward
		ActionForward forward=mapping.findForward("next");
		// If we got one, modify it to include the image ID.
		if(forward!=null) {
			String path=forward.getPath();
			path+="?id=" + imageId;
			// Duplicate the forward since modifying it does bad things.
			rv=new ActionForward(path);
			rv.setName(forward.getName());
			rv.setRedirect(forward.getRedirect());
		}
		return(rv);
	}

}

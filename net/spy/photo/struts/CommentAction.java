// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: CommentAction.java,v 1.2 2002/07/10 03:38:09 dustin Exp $

package net.spy.photo.struts;

import java.io.IOException;

import javax.servlet.ServletException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.spy.photo.Comment;
import net.spy.photo.Persistent;
import net.spy.photo.PhotoSessionData;
import net.spy.photo.PhotoUser;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

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
	 * Process the forgotten password request.
	 */
	public ActionForward perform(ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,HttpServletResponse response)
		throws IOException, ServletException {

		CommentForm cf=(CommentForm)form;

        // Get the session data
        PhotoSessionData sessionData=getSessionData(request);
        // Get the user
        PhotoUser user=sessionData.getUser();

        int imageId=Integer.parseInt(cf.getImageId());

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
        comment.setNote(cf.getComment());

        try {
            comment.save();
        } catch(Exception e) {
            throw new ServletException("Error saving comment", e);
        }

        // Get the configured forward
        ActionForward forward=mapping.findForward("success");
        // If we got one, modify it to include the image ID.
        if(forward!=null) {
            String path=forward.getPath();
            path+="?id=" + imageId;
            forward.setPath(path);
        }
		return(forward);
	}

}

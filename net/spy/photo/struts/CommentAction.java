// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: CommentAction.java,v 1.1 2002/06/17 19:51:38 dustin Exp $

package net.spy.photo.struts;

import java.io.*;
import java.util.*;
import java.sql.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.apache.struts.action.*;

import net.spy.SpyDB;
import net.spy.util.PwGen;

import net.spy.photo.*;

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

// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: UploadAction.java,v 1.3 2002/06/03 06:49:22 dustin Exp $

package net.spy.photo.struts;

import java.io.IOException;
import java.sql.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.apache.struts.action.*;

import net.spy.*;

import net.spy.photo.*;

/**
 * The action performed when an image is uploaded.
 */
public class UploadAction extends PhotoAction {

	/**
	 * Get an instance of UploadAction.
	 */
	public UploadAction() {
		super();
	}

	public ActionForward perform(ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,HttpServletResponse response)
		throws IOException, ServletException {

		UploadForm uf=(UploadForm)form;
		// Get the session data.
		PhotoSessionData sessionData=getSessionData(request);
		// Verify upload permission.
		int cat=Integer.parseInt(uf.getCategory());

		// Get the user, and verify he/she can add to the requested category
		PhotoUser user=sessionData.getUser();
		if(!user.canAdd(cat)) {
			throw new ServletException(
				sessionData.getUser() + " can't add to category " + cat);
		}

		try {

			// Get the ID
			int id=PhotoSaver.getNewImageId();
			PhotoSaver saver=new PhotoSaver();

			saver.setKeywords(uf.getKeywords());
			saver.setInfo(uf.getInfo());
			saver.setCat(cat);
			saver.setTaken(uf.getTaken());
			saver.setUser(user);
			saver.setPhotoImage(uf.getPhotoImage());
			saver.setId(id);

			saver.saveImage();

			Persistent.logger.log(new PhotoLogUploadEntry(
				user.getId(), id, request));

			// Before we return, make the ID available to the next handler
			request.setAttribute("net.spy.photo.UploadID", new Integer(id));
		} catch(PhotoException e) {
			throw new ServletException("Error saving image.", e);
		}
		return(mapping.findForward("success"));
	}

}

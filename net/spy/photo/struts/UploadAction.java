// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: UploadAction.java,v 1.7 2003/05/25 08:17:42 dustin Exp $

package net.spy.photo.struts;

import java.io.IOException;

import javax.servlet.ServletException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.spy.photo.Persistent;
import net.spy.photo.PhotoException;
import net.spy.photo.PhotoLogUploadEntry;
import net.spy.photo.PhotoSaver;
import net.spy.photo.PhotoSessionData;
import net.spy.photo.PhotoUser;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

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

	public ActionForward execute(ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,HttpServletResponse response)
		throws Exception {

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

		// Tell the saver to save this image when it gets around to it.
		Persistent.getPhotoSaverThread().saveImage(saver);

		Persistent.getLogger().log(new PhotoLogUploadEntry(
			user.getId(), id, request));

		// Before we return, make the ID available to the next handler
		request.setAttribute("net.spy.photo.UploadID", new Integer(id));

		return(mapping.findForward("success"));
	}

}

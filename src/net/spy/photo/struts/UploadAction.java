// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
// arch-tag: 3DEBB7CA-5D6E-11D9-8B80-000A957659CC

package net.spy.photo.struts;

import java.io.IOException;

import javax.servlet.ServletException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.spy.db.Saver;

import net.spy.photo.Persistent;
import net.spy.photo.PhotoException;
import net.spy.photo.PhotoLogUploadEntry;
import net.spy.photo.PhotoSessionData;
import net.spy.photo.PhotoUser;
import net.spy.photo.PhotoConfig;
import net.spy.photo.impl.SavablePhotoImageData;

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

	public ActionForward spyExecute(ActionMapping mapping,
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

		// Get the Savable
		SavablePhotoImageData savable=
			new SavablePhotoImageData(uf.getPhotoImage());

		savable.setKeywords(uf.getKeywords());
		savable.setDescr(uf.getInfo());
		savable.setCatId(cat);
		savable.setTaken(uf.getTaken());
		savable.setAddedBy(user);

		Saver s=new Saver(PhotoConfig.getInstance());
		s.save(savable);

		int id=savable.getId();

		Persistent.getPipeline().addTransaction(new PhotoLogUploadEntry(
			user.getId(), id, request), PhotoConfig.getInstance());

		// Before we return, make the ID available to the next handler
		request.setAttribute("net.spy.photo.UploadID", new Integer(id));

		return(mapping.findForward("next"));
	}

}

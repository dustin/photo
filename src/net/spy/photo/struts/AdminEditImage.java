// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: AdminEditImage.java,v 1.6 2003/07/23 04:29:26 dustin Exp $

package net.spy.photo.struts;

import java.io.IOException;

import javax.servlet.ServletException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.spy.db.Saver;

import net.spy.photo.PhotoConfig;
import net.spy.photo.PhotoImageDataImpl;
import net.spy.photo.SavablePhotoImageData;
import net.spy.photo.PhotoImageDataFactory;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * Edit images.
 */
public class AdminEditImage extends PhotoAction {

	/**
	 * Get an instance of AdminEditImage.
	 */
	public AdminEditImage() {
		super();
	}

	/**
	 * Perform the action.
	 */
	public ActionForward spyExecute(ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,HttpServletResponse response)
		throws Exception {

		UploadForm uf=(UploadForm)form;

		PhotoImageDataFactory pidf=PhotoImageDataFactory.getInstance();

		SavablePhotoImageData savable=new SavablePhotoImageData(
			pidf.getData(Integer.parseInt(uf.getId())));
		savable.setDescr(uf.getInfo());
		savable.setKeywords(uf.getKeywords());
		savable.setCatId(Integer.parseInt(uf.getCategory()));
		savable.setTaken(uf.getTaken());

		Saver s=new Saver(PhotoConfig.getInstance());
		s.save(savable);

		return(mapping.findForward("next"));
	}

}

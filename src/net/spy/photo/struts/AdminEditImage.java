// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
// arch-tag: 090095B6-5D6E-11D9-9A76-000A957659CC

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

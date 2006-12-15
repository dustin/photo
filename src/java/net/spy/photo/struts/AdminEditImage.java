// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>

package net.spy.photo.struts;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.spy.photo.PhotoImageDataFactory;
import net.spy.photo.impl.SavablePhotoImageData;

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
	@Override
	public ActionForward spyExecute(ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,HttpServletResponse response)
		throws Exception {

		UploadForm uf=(UploadForm)form;

		PhotoImageDataFactory pidf=PhotoImageDataFactory.getInstance();

		SavablePhotoImageData savable=new SavablePhotoImageData(
			pidf.getObject(Integer.parseInt(uf.getId())));
		savable.setDescr(uf.getInfo());
		savable.setKeywords(uf.getKeywords());
		savable.setCatId(Integer.parseInt(uf.getCategory()));
		savable.setTaken(uf.getTaken());

		pidf.store(savable);

		return(mapping.findForward("next"));
	}

}

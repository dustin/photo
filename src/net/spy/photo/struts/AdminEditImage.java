// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: AdminEditImage.java,v 1.6 2003/07/23 04:29:26 dustin Exp $

package net.spy.photo.struts;

import java.io.IOException;

import javax.servlet.ServletException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.spy.photo.PhotoConfig;

import net.spy.photo.sp.EditImage;

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

		try {
			EditImage ei=new EditImage(PhotoConfig.getInstance());

			ei.setKeywords(uf.getKeywords());
			ei.setDescr(uf.getInfo());
			ei.setCat(Integer.parseInt(uf.getCategory()));
			ei.setTaken(uf.getTaken());
			ei.setId(Integer.parseInt(uf.getId()));

			ei.executeUpdate();
			ei.close();
		} catch(Exception e) {
			throw new ServletException("Error saving image data", e);
		}

		return(mapping.findForward("next"));
	}

}

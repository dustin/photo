// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: AdminEditImage.java,v 1.3 2002/09/14 05:06:34 dustin Exp $

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
public class AdminEditImage extends AdminAction {

	/**
	 * Get an instance of AdminEditImage.
	 */
	public AdminEditImage() {
		super();
	}

	/**
	 * Perform the action.
	 */
	public ActionForward perform(ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,HttpServletResponse response)
		throws IOException, ServletException {

		// Verify the user is an admin or subadmin
		checkAdminOrSubadmin(request);

		UploadForm uf=(UploadForm)form;

		try {
			EditImage ei=new EditImage(new PhotoConfig());

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

		return(mapping.findForward("success"));
	}

}

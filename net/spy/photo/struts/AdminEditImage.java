// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: AdminEditImage.java,v 1.1 2002/06/23 06:15:16 dustin Exp $

package net.spy.photo.struts;

import java.sql.*;
import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.apache.struts.action.*;

import net.spy.photo.*;
import net.spy.photo.sp.*;

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

			ei.set("keywords", uf.getKeywords());
			ei.set("descr", uf.getInfo());
			ei.set("cat", Integer.parseInt(uf.getCategory()));
			ei.set("taken", uf.getTaken());
			ei.set("id", Integer.parseInt(uf.getId()));

			ei.executeUpdate();
			ei.close();
		} catch(Exception e) {
			throw new ServletException("Error saving image data", e);
		}

		return(mapping.findForward("success"));
	}

}

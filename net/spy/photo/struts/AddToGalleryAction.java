// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: AddToGalleryAction.java,v 1.1 2002/07/01 07:07:54 dustin Exp $

package net.spy.photo.struts;

import java.io.*;
import java.util.*;
import java.sql.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.apache.struts.action.*;

import net.spy.SpyDB;

import net.spy.photo.*;

/**
 * Action to add an image to a gallery.
 *
 * If a new gallery is not currently being created, a new gallery will be
 * created.
 */
public class AddToGalleryAction extends PhotoAction {

	/**
	 * Get an instance of AddToGalleryAction.
	 */
	public AddToGalleryAction() {
		super();
	}

	/**
	 * Perform the action.
	 */
	public ActionForward perform(ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,HttpServletResponse response)
		throws IOException, ServletException {

		AddToGalleryForm atgf=(AddToGalleryForm)form;

		PhotoSessionData sessionData=getSessionData(request);
		PhotoUser user=sessionData.getUser();

		HttpSession session=request.getSession(false);

		Gallery g=(Gallery)session.getAttribute("newGallery");
		if(g==null) {
			// If we don't have a gallery yet, get a new one.
			g=new Gallery(user);
		}

		// Add the new image
		try {
			g.addImage(atgf.getImageId());
		} catch(Exception e) {
			throw new ServletException("Error adding new image", e);
		}

		return(mapping.findForward("success"));
	}

}

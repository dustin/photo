// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: AddToGalleryAction.java,v 1.8 2003/07/14 06:21:28 dustin Exp $

package net.spy.photo.struts;

import java.io.IOException;

import javax.servlet.ServletException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.spy.photo.Gallery;
import net.spy.photo.PhotoSessionData;
import net.spy.photo.PhotoUser;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.DynaActionForm;

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
	public ActionForward execute(ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,HttpServletResponse response)
		throws Exception {

		DynaActionForm atgf=(DynaActionForm)form;

		PhotoSessionData sessionData=getSessionData(request);
		PhotoUser user=sessionData.getUser();

		HttpSession session=request.getSession(false);

		Gallery g=(Gallery)session.getAttribute("newGallery");
		if(g==null) {
			log("Creating new gallery.");
			// If we don't have a gallery yet, get a new one.
			g=new Gallery(user);
			// Add it to the session.
			session.setAttribute("newGallery", g);
		}

		// Add the new image
		try {
			Integer id=(Integer)atgf.get("id");
			g.addImage(id.intValue());
		} catch(Exception e) {
			throw new ServletException("Error adding new image", e);
		}

		return(mapping.findForward("next"));
	}

}

// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: SaveGalleryAction.java,v 1.5 2002/07/10 03:38:09 dustin Exp $

package net.spy.photo.struts;

import java.io.IOException;

import javax.servlet.ServletException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.spy.photo.Gallery;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * Action to save a gallery.
 */
public class SaveGalleryAction extends PhotoAction {

	/**
	 * Get an instance of SaveGalleryAction.
	 */
	public SaveGalleryAction() {
		super();
	}

	/**
	 * Perform the action.
	 */
	public ActionForward perform(ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,HttpServletResponse response)
		throws IOException, ServletException {

		SaveGalleryForm sgf=(SaveGalleryForm)form;

		HttpSession session=request.getSession(false);

		Gallery g=(Gallery)session.getAttribute("newGallery");
		if(g==null) {
			throw new ServletException(
				"No gallery in session, nothing to save!");
		}
		if(g.size() == 0) {
			throw new ServletException(
				"Sorry, I won't save an empty gallery.");
		}

		g.setName(sgf.getName());
		g.setPublic(sgf.getIsPublic());

		// Add the new image
		try {
			g.save();
		} catch(Exception e) {
			throw new ServletException("Error saving gallery", e);
		}

		// Throw it away, freeing us up to create a new gallery.
		session.removeAttribute("newGallery");

		return(mapping.findForward("success"));
	}

}

// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: SaveGalleryAction.java,v 1.9 2003/07/23 04:29:26 dustin Exp $

package net.spy.photo.struts;

import java.io.IOException;

import javax.servlet.ServletException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import net.spy.db.Saver;

import net.spy.photo.PhotoConfig;
import net.spy.photo.Gallery;

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
	public ActionForward spyExecute(ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,HttpServletResponse response)
		throws Exception {

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
		Saver saver=new Saver(PhotoConfig.getInstance());
		saver.save(g);

		// Throw it away, freeing us up to create a new gallery.
		session.removeAttribute("newGallery");

		return(mapping.findForward("next"));
	}

}

// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: SaveGalleryAction.java,v 1.2 2002/07/02 07:03:41 dustin Exp $

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

		PhotoSessionData sessionData=getSessionData(request);
		PhotoUser user=sessionData.getUser();

		HttpSession session=request.getSession(false);

		Gallery g=(Gallery)session.getAttribute("newGallery");
		if(g==null) {
			throw new ServletException(
				"No gallery in session, nothing to save!");
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

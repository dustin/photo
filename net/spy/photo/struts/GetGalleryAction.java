// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: GetGalleryAction.java,v 1.1 2002/07/02 00:01:21 dustin Exp $

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
 * Fetch a cursor for a gallery.
 */
public class GetGalleryAction extends PhotoAction {

	/**
	 * Get an instance of GetGalleryAction.
	 */
	public GetGalleryAction() {
		super();
	}

	/**
	 * Perform the action.
	 */
	public ActionForward perform(ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,HttpServletResponse response)
		throws IOException, ServletException {

		GetGalleryForm ggf=(GetGalleryForm)form;

		PhotoSessionData sessionData=getSessionData(request);
		PhotoUser user=sessionData.getUser();

		try {
			Gallery g=Gallery.getGallery(user, ggf.getId());
			sessionData.setCursor("gallery", new Cursor(g.getImages()));
		} catch(Exception e) {
			throw new ServletException("Couldn't get gallery", e);
		}

		return(mapping.findForward("success"));
	}

}

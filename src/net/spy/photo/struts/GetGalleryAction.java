// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: GetGalleryAction.java,v 1.5 2003/07/23 04:29:26 dustin Exp $

package net.spy.photo.struts;

import java.io.IOException;

import javax.servlet.ServletException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.spy.photo.Cursor;
import net.spy.photo.Gallery;
import net.spy.photo.PhotoSessionData;
import net.spy.photo.PhotoUser;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

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
	public ActionForward spyExecute(ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,HttpServletResponse response)
		throws Exception {

		GetGalleryForm ggf=(GetGalleryForm)form;

		PhotoSessionData sessionData=getSessionData(request);
		PhotoUser user=sessionData.getUser();

		Gallery g=Gallery.getGallery(user, ggf.getId());
		sessionData.setCursor("gallery", new Cursor(g.getImages()));

		return(mapping.findForward("next"));
	}

}

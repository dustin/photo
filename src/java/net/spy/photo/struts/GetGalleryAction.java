// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>

package net.spy.photo.struts;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.spy.photo.Cursor;
import net.spy.photo.Gallery;
import net.spy.photo.PhotoImageData;
import net.spy.photo.PhotoSessionData;
import net.spy.photo.User;

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
	@Override
	public ActionForward spyExecute(ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,HttpServletResponse response)
		throws Exception {

		GetGalleryForm ggf=(GetGalleryForm)form;

		PhotoSessionData sessionData=getSessionData(request);
		User user=sessionData.getUser();

		Gallery g=Gallery.getGallery(user, ggf.getId());
		sessionData.setCursor("gallery",
				new Cursor<PhotoImageData>(g.getImages()));

		return(mapping.findForward("next"));
	}

}

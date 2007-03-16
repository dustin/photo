// Copyright (c) 2003  Dustin Sallings <dustin@spy.net>

package net.spy.photo.struts;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.spy.photo.Persistent;
import net.spy.photo.PhotoException;
import net.spy.photo.PhotoImage;
import net.spy.photo.PhotoImageFactory;
import net.spy.photo.PhotoSessionData;
import net.spy.photo.PhotoUtil;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.DynaActionForm;

/**
 * Action to retrieve image data.
 */
public class GetImageDataAction extends PhotoAction {

	private static final String ATTRIBUTE="image";
	private static final String DIM_ATTR="displayDims";
	private static final String MY_RATING="myrating";

	private PhotoImage getImageById(int id, HttpServletRequest req) {
		PhotoImage rv=null;
		try {
			PhotoImageFactory pidf=PhotoImageFactory.getInstance();
			rv=pidf.getObject(id);
		} catch(PhotoImageFactory.NoSuchPhotoException e) {
			getLogger().info("Requested missing photo", e);
		}
		return(rv);
	}

	/** 
	 * Process the execute.
	 */
	@Override
	public ActionForward spyExecute(ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,HttpServletResponse response)
		throws Exception {

		// Figure out what's there.
		DynaActionForm f=(DynaActionForm)form;

		Integer imageId=(Integer)f.get("id");
		if(imageId == null) {
			imageId = (Integer)f.get("image_id");
		}

		if(imageId == null) {
			throw new PhotoException("Need an ID.");
		}

		// OK, now figure out what kind we want
		PhotoImage image=getImageById(imageId.intValue(), request);

		ActionForward rv=mapping.findForward("next");

		if(image == null) {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			rv=mapping.findForward("notfound");
		} else {

			// Check access
			PhotoSessionData sessionData=getSessionData(request);
			boolean hasAccess=Persistent.getSecurity().testAccess(
				sessionData.getUser(), image.getId());

			if(!hasAccess) {
				response.setStatus(HttpServletResponse.SC_FORBIDDEN);
				rv=mapping.findForward("forbidden");
			} else {
				request.setAttribute(ATTRIBUTE, image);

				// We will need the display dimensions for some tasks
				request.setAttribute(DIM_ATTR,
					PhotoUtil.scaleTo(image.getDimensions(),
					sessionData.getOptimalDimensions()));

				request.setAttribute(MY_RATING, image.getVotes().getVote(
					sessionData.getUser().getId()));
			}
		}
		return(rv);
	}

}

// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
// arch-tag: 6DB1E024-411A-4242-83BE-A2E1A9D315AF

package net.spy.photo.struts;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.DynaActionForm;

import net.spy.photo.Persistent;
import net.spy.photo.PhotoDimensions;
import net.spy.photo.PhotoImageDataFactory;
import net.spy.photo.PhotoRegion;
import net.spy.photo.PhotoSessionData;
import net.spy.photo.PhotoUtil;
import net.spy.photo.User;
import net.spy.photo.impl.PhotoDimensionsImpl;
import net.spy.photo.impl.PhotoRegionImpl;
import net.spy.photo.impl.SavablePhotoImageData;

/**
 * Action that submits an annotated region.
 */
public class AnnotateAction extends PhotoAction {

	/**
	 * Get an instance of AnnotateAction.
	 */
	public AnnotateAction() {
		super();
	}

	/**
	 * Process the annotation thing.
	 */
	public ActionForward spyExecute(
		ActionMapping mapping, ActionForm form,
		HttpServletRequest request, HttpServletResponse response) 
		throws Exception {

		DynaActionForm df=(DynaActionForm)form;

		// Get the session data
		PhotoSessionData sessionData=getSessionData(request);
		// Get the user
		User user=sessionData.getUser();

		Integer imageInteger=(Integer)df.get("imageId");
		int imageId=imageInteger.intValue();

		// Check permission
		try {
			Persistent.getSecurity().checkAccess(user, imageId);
		} catch(Exception e) {
			throw new ServletException(e.getMessage(), e);
		}

		PhotoImageDataFactory pidf=PhotoImageDataFactory.getInstance();
		SavablePhotoImageData savable=new SavablePhotoImageData(
			pidf.getObject(imageId));

		// Get the dimensions of the image as displayed
		PhotoDimensions displaySize=
			new PhotoDimensionsImpl((String)df.get("imgDims"));
		float scaleFactor=PhotoUtil.getScaleFactor(displaySize,
			savable.getDimensions());

		// Get the region as specified
		PhotoRegion specifiedRegion=new PhotoRegionImpl(
			((Integer)df.get("x")).intValue(),
			((Integer)df.get("y")).intValue(),
			((Integer)df.get("w")).intValue(),
			((Integer)df.get("h")).intValue());

		// The scaled region
		PhotoRegion newRegion=PhotoUtil.scaleRegion(specifiedRegion,
			scaleFactor);

		// Set the annotation
		savable.addAnnotation(
			newRegion.getX(), newRegion.getY(),
			newRegion.getWidth(), newRegion.getHeight(),
			(String)df.get("keywords"),
			(String)df.get("title"), user);

		// Store it
		pidf.store(savable);

		ActionForward rv=null;

		// Get the configured forward
		ActionForward forward=mapping.findForward("next");
		// If we got one, modify it to include the image ID.
		if(forward!=null) {
			String path=forward.getPath();
			path+="?id=" + imageId;
			// Duplicate the forward since modifying it does bad things.
			rv=new ActionForward(path);
			rv.setName(forward.getName());
			rv.setRedirect(forward.getRedirect());
		}
		return(rv);
	}

}

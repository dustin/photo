// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: AddToGalleryForm.java,v 1.1 2002/07/01 07:07:54 dustin Exp $

package net.spy.photo.struts;

import javax.servlet.http.HttpServletRequest;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

/**
 * Form for adding to galleries.
 */
public class AddToGalleryForm extends ActionForm {

	private int imageId=-1;

	/**
	 * Get an instance of AddToGalleryForm.
	 */
	public AddToGalleryForm() {
		super();
	}

	/**
	 * Get the image ID the form is mapping.
	 */
	public int getImageId() {
		return(imageId);
	}

	/**
	 * Set the image ID to add to the gallery.
	 */
	public void setImageId(int imageId) {
		this.imageId=imageId;
	}

	/**
	 * Validate the input.
	 */
	public ActionErrors validate(ActionMapping mapping,
		HttpServletRequest request) {

		ActionErrors errors = new ActionErrors();

		if(imageId==-1) {
			errors.add("imageId",
				new ActionError("error.addtogallery.imageid"));
		}

		return(errors);
	}

}

// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: AddToGalleryForm.java,v 1.2 2002/07/01 18:03:19 dustin Exp $

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

	private int id=-1;

	/**
	 * Get an instance of AddToGalleryForm.
	 */
	public AddToGalleryForm() {
		super();
	}

	/**
	 * Get the image ID the form is mapping.
	 */
	public int getId() {
		return(id);
	}

	/**
	 * Set the image ID to add to the gallery.
	 */
	public void setId(int id) {
		this.id=id;
	}

	/**
	 * Validate the input.
	 */
	public ActionErrors validate(ActionMapping mapping,
		HttpServletRequest request) {

		ActionErrors errors = new ActionErrors();

		if(id==-1) {
			errors.add("id", new ActionError("error.addtogallery.id"));
		}

		return(errors);
	}

}

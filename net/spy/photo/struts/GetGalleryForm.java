// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: GetGalleryForm.java,v 1.1 2002/07/02 00:01:21 dustin Exp $

package net.spy.photo.struts;

import javax.servlet.http.HttpServletRequest;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

/**
 * Form for fetching a gallery.
 */
public class GetGalleryForm extends ActionForm {

	private int id=-1;

	/**
	 * Get an instance of GetGalleryForm.
	 */
	public GetGalleryForm() {
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
			errors.add("id", new ActionError("error.getgallery.id"));
		}

		return(errors);
	}

}

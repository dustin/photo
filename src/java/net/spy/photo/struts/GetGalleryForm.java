// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>

package net.spy.photo.struts;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;

/**
 * Form for fetching a gallery.
 */
public class GetGalleryForm extends PhotoForm {

	private int id=-1;

	/**
	 * Get the image ID the form is mapping.
	 */
	public int getId() {
		return(id);
	}

	/**
	 * Set the image ID to add to the gallery.
	 */
	public void setId(int to) {
		this.id=to;
	}

	/**
	 * Validate the input.
	 */
	@Override
	public ActionErrors validate(ActionMapping mapping,
		HttpServletRequest request) {

		ActionErrors errors = new ActionErrors();

		if(id==-1) {
			errors.add("id", new ActionMessage("error.getgallery.id"));
		}

		return(errors);
	}

}

// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: SaveGalleryForm.java,v 1.1 2002/07/01 23:50:43 dustin Exp $

package net.spy.photo.struts;

import javax.servlet.http.HttpServletRequest;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

/**
 * Form for saving a gallery.
 */
public class SaveGalleryForm extends ActionForm {

	private String name=null;

	/**
	 * Get an instance of SaveGalleryForm.
	 */
	public SaveGalleryForm() {
		super();
	}

	/**
	 *Set the name for the gallery.
	 */
	public void setName(String name) {
		this.name=name;
	}

	/**
	 * Get the name of the gallery.
	 */
	public String getName() {
		return(name);
	}

	/**
	 * Validate the input.
	 */
	public ActionErrors validate(ActionMapping mapping,
		HttpServletRequest request) {

		ActionErrors errors = new ActionErrors();

		if(name==null) {
			errors.add("name", new ActionError("error.savegallery.name"));
		}

		return(errors);
	}

}

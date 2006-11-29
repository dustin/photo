// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
// arch-tag: 357B523C-5D6E-11D9-B3F4-000A957659CC

package net.spy.photo.struts;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;

/**
 * Form for saving a gallery.
 */
public class SaveGalleryForm extends PhotoForm {

	private String name=null;
	private boolean isPublic=false;

	/**
	 * Get an instance of SaveGalleryForm.
	 */
	public SaveGalleryForm() {
		super();
	}

	/**
	 *Set the name for the gallery.
	 */
	public void setName(String to) {
		this.name=to;
	}

	/**
	 * Get the name of the gallery.
	 */
	public String getName() {
		return(name);
	}

	/**
	 * True if the gallery is to be public.
	 */
	public boolean getIsPublic() {
		return(isPublic);
	}

	/**
	 * Set to true if the gallery is to be public.
	 */
	public void setIsPublic(boolean to) {
		this.isPublic=to;
	}

	/**
	 * Validate the input.
	 */
	@Override
	public ActionErrors validate(ActionMapping mapping,
		HttpServletRequest request) {

		ActionErrors errors = new ActionErrors();

		if(name==null) {
			errors.add("name", new ActionMessage("error.savegallery.name"));
		}

		return(errors);
	}

}
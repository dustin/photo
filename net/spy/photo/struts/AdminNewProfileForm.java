// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: AdminNewProfileForm.java,v 1.1 2002/06/23 01:38:47 dustin Exp $

package net.spy.photo.struts;

import javax.servlet.http.HttpServletRequest;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

/**
 * Form to create new profiles.
 */
public class AdminNewProfileForm extends ActionForm {

	private String name=null;
	private String categories[]=null;

	/**
	 * Get an instance of AdminNewProfileForm.
	 */
	public AdminNewProfileForm() {
		super();
	}

	public String getName() {
		return(name);
	}

	public void setName(String name) {
		this.name=name;
	}

	public String[] getCategories() {
		return(categories);
	}

	public void setCategories(String[] categories) {
		this.categories=categories;
	}

	/**
	 * Validate the input.
	 */
	public ActionErrors validate(ActionMapping mapping,
		HttpServletRequest request) {

		ActionErrors errors = new ActionErrors();

		if(name==null || name.length() < 1) {
			errors.add("name",
				new ActionError("error.adminnewprofileform.name"));
		}
		if(categories==null) {
			errors.add("categories",
				new ActionError("error.adminnewprofileform.categories"));
		}

		return(errors);
	}

}

// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>

package net.spy.photo.struts;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;

/**
 * Form to create new profiles.
 */
public class AdminNewProfileForm extends PhotoForm {

	private String name=null;
	private String categories[]=null;

	public String getName() {
		return(name);
	}

	public void setName(String to) {
		this.name=to;
	}

	public String[] getCategories() {
		return(categories);
	}

	public void setCategories(String[] to) {
		this.categories=to;
	}

	/**
	 * Validate the input.
	 */
	@Override
	public ActionErrors validate(ActionMapping mapping,
		HttpServletRequest request) {

		ActionErrors errors = new ActionErrors();

		if(name==null || name.length() < 1) {
			errors.add("name",
				new ActionMessage("error.adminnewprofileform.name"));
		}
		if(categories==null) {
			errors.add("categories",
				new ActionMessage("error.adminnewprofileform.categories"));
		}

		return(errors);
	}

}

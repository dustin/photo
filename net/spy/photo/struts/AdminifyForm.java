// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: AdminifyForm.java,v 1.2 2002/07/10 03:38:09 dustin Exp $

package net.spy.photo.struts;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

/**
 * Form for adminifying and deadminifying a user.
 */
public class AdminifyForm extends ActionForm {

	private String action=null;

	/**
	 * Get an instance of AdminifyForm.
	 */
	public AdminifyForm() {
		super();
	}

	/**
	 * Set the action property.
	 */
	public void setAction(String action) {
		this.action=action;
	}

	/**
	 * Get the action property.
	 */
	public String getAction() {
		return(action);
	}

	/**
	 * Reset all properties to their default values.
	 */
	public void reset(ActionMapping mapping, HttpServletRequest request) {
		action=null;
	}

	/**
	 * Validate the properties.
	 */
	public ActionErrors validate(ActionMapping mapping,
		HttpServletRequest request) {

		ActionErrors errors = new ActionErrors();

		if(action==null
			|| (!action.equals("setadmin")
				&& !action.equals("unsetadmin"))) {
			errors.add("action", new ActionError("error.adminify.invaction"));
		}

		return(errors);
	}

}

// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: ChangePWForm.java,v 1.2 2002/07/10 03:38:09 dustin Exp $

package net.spy.photo.struts;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

/**
 * Form used for changing user password.
 */
public class ChangePWForm extends ActionForm {

	private String oldpw=null;
	private String newpw1=null;
	private String newpw2=null;

	/**
	 * Get an instance of ChangePWForm.
	 */
	public ChangePWForm() {
		super();
	}

	public String getOldpw() {
		return(oldpw);
	}

	public void setOldpw(String oldpw) {
		this.oldpw=oldpw;
	}

	public String getNewpw1() {
		return(newpw1);
	}

	public void setNewpw1(String newpw1) {
		this.newpw1=newpw1;
	}

	public String getNewpw2() {
		return(newpw2);
	}

	public void setNewpw2(String newpw2) {
		this.newpw2=newpw2;
	}

	/**
	 * Reset all properties.
	 */
	public void reset(ActionMapping mapping, HttpServletRequest request) {
		oldpw=null;
		newpw1=null;
		newpw2=null;
	}

	/**
	 * Validate the input.
	 */
	public ActionErrors validate(ActionMapping mapping,
		HttpServletRequest request) {

		ActionErrors errors = new ActionErrors();

		if(oldpw==null || oldpw.length() < 1) {
			errors.add("oldpw", new ActionError("error.changepw.oldpw"));
		}

		if(newpw1==null || newpw1.length() < 6) {
			errors.add("newpw1", new ActionError("error.changepw.newpw1"));
		}

		if(newpw1!=null && newpw2!=null && !(newpw1.equals(newpw2))) {
			errors.add("newpw1", new ActionError("error.changepw.mismatch"));
		}

		return(errors);
	}

}

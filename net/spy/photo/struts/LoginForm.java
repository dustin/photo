// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: LoginForm.java,v 1.7 2002/11/04 03:11:24 dustin Exp $

package net.spy.photo.struts;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

/**
 * Form for processing login requests.
 */
public class LoginForm extends ActionForm {

	private String username=null;
	private String password=null;
	private boolean admin=false;

	/**
	 * Get an instance of LoginForm.
	 */
	public LoginForm() {
		super();
	}

	/**
	 * Get the username parameter.
	 */
	public String getUsername() {
		return(username);
	}

	/**
	 * Set the username password.
	 */
	public void setUsername(String username) {
		this.username=username;
	}

	/**
	 * Get the password parameter.
	 */
	public String getPassword() {
		return(password);
	}

	/**
	 * Set the password parameter.
	 */
	public void setPassword(String password) {
		this.password=password;
	}

	/**
	 * If true, the user would also like his admin status.
	 */
	public void setAdmin(boolean admin) {
		this.admin=admin;
	}

	/**
	 * If true, the user would also like his admin status.
	 */
	public boolean getAdmin() {
		return(admin);
	}

	/**
	 * Validate the properties.
	 *
	 * @param mapping The mapping used to select this instance
	 * @param request The servlet request we are processing
	 */
	public ActionErrors validate(ActionMapping mapping,
		HttpServletRequest request) {

		ActionErrors errors = new ActionErrors();
		if ((username == null) || (username.length() < 1)) {
			errors.add("username", new ActionError("error.login.username"));
		}
		if ((password == null) || (password.length() < 1)) {
			errors.add("password", new ActionError("error.login.password"));
		}

		return(errors);
	}

	/** 
	 * Out with the old, in with the new.
	 * 
	 * @param mapping servlet mapping being used
	 * @param request the request objectg
	 */
	public void reset(ActionMapping mapping, HttpServletRequest request) {
		admin=false;
		password=null;
	}

}

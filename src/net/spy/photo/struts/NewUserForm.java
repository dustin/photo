// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: NewUserForm.java,v 1.2 2002/07/10 03:38:09 dustin Exp $

package net.spy.photo.struts;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

/**
 * Form used when creating an account.
 */
public class NewUserForm extends PhotoForm {

	private String username=null;
	private String password=null;
	private String pass2=null;
	private String realname=null;
	private String email=null;
	private String profile=null;

	/**
	 * Get an instance of NewUserForm.
	 */
	public NewUserForm() {
		super();
	}

	public void setUsername(String username) {
		this.username=username;
	}

	public String getUsername() {
		return(username);
	}

	public void setPassword(String password) {
		this.password=password;
	}

	public String getPassword() {
		return(password);
	}

	public void setPass2(String pass2) {
		this.pass2=pass2;
	}

	public String getPass2() {
		return(pass2);
	}

	public void setRealname(String realname) {
		this.realname=realname;
	}

	public String getRealname() {
		return(realname);
	}

	public void setEmail(String email) {
		this.email=email;
	}

	public String getEmail() {
		return(email);
	}

	public void setProfile(String profile) {
		this.profile=profile;
	}

	public String getProfile() {
		return(profile);
	}

	/**
	 * Validate the input.
	 */
	public ActionErrors validate(ActionMapping mapping,
		HttpServletRequest request) {

		ActionErrors errors = new ActionErrors();

		if((username == null) || (username.length() < 1)) {
			errors.add("username", new ActionError("error.newuser.username"));
		}

		if((realname == null) || (realname.length() < 3)
			|| (realname.indexOf(" ")==-1)) {
			errors.add("realname", new ActionError("error.newuser.realname"));
		}

		if((password==null) || (password.length() < 6)) {
			errors.add("password", new ActionError("error.newuser.password"));
		}

		if((pass2==null) || (!pass2.equals(password))) {
			errors.add("pass2", new ActionError("error.newuser.pwmismatch"));
		}

		if((email==null) || (email.length()<9)) {
			errors.add("email", new ActionError("error.newuser.email"));
		}

		return(errors);
	}

}

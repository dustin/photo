// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
// arch-tag: 30B6C8FF-5D6E-11D9-A6BA-000A957659CC

package net.spy.photo.struts;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;

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

	public void setUsername(String to) {
		this.username=to;
	}

	public String getUsername() {
		return(username);
	}

	public void setPassword(String to) {
		this.password=to;
	}

	public String getPassword() {
		return(password);
	}

	public void setPass2(String to) {
		this.pass2=to;
	}

	public String getPass2() {
		return(pass2);
	}

	public void setRealname(String to) {
		this.realname=to;
	}

	public String getRealname() {
		return(realname);
	}

	public void setEmail(String to) {
		this.email=to;
	}

	public String getEmail() {
		return(email);
	}

	public void setProfile(String to) {
		this.profile=to;
	}

	public String getProfile() {
		return(profile);
	}

	/**
	 * Validate the input.
	 */
	@Override
	public ActionErrors validate(ActionMapping mapping,
		HttpServletRequest request) {

		ActionErrors errors = new ActionErrors();

		if((username == null) || (username.length() < 1)) {
			errors.add("username", new ActionMessage("error.newuser.username"));
		}

		if((realname == null) || (realname.length() < 3)
			|| (realname.indexOf(" ")==-1)) {
			errors.add("realname", new ActionMessage("error.newuser.realname"));
		}

		if((password==null) || (password.length() < 6)) {
			errors.add("password", new ActionMessage("error.newuser.password"));
		}

		if((pass2==null) || (!pass2.equals(password))) {
			errors.add("pass2", new ActionMessage("error.newuser.pwmismatch"));
		}

		if((email==null) || (email.length()<9)) {
			errors.add("email", new ActionMessage("error.newuser.email"));
		}

		return(errors);
	}

}

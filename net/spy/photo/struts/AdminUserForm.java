// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: AdminUserForm.java,v 1.1 2002/06/22 08:27:07 dustin Exp $

package net.spy.photo.struts;

import javax.servlet.http.HttpServletRequest;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

/**
 * Form used by an admin to edit a user.
 */
public class AdminUserForm extends ActionForm {

	private String userId=null;
	private String username=null;
	private String realname=null;
	private String email=null;
	private String canadd=null;
	private String catAclAdd[]=null;
	private String catAclView[]=null;

	/**
	 * Get an instance of AdminUserForm.
	 */
	public AdminUserForm() {
		super();
	}

	/**
	 * Get the string representation of the user ID.
	 */
	public String getUserId() {
		return(userId);
	}

	/**
	 * Set the string representation of the user ID.
	 */
	public void setUserId(String userId) {
		this.userId=userId;
	}

	public void setUsername(String username) {
		this.username=username;
	}

	public String getUsername() {
		return(username);
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

	public String getCanadd() {
		return(canadd);
	}

	public String[] getCatAclAdd() {
		return(catAclAdd);
	}

	public String[] getCatAclView() {
		return(catAclView);
	}

	public void setCanadd(String canadd) {
		this.canadd=canadd;
	}

	public void setCatAclAdd(String catAclAdd[]) {
		this.catAclAdd=catAclAdd;
	}

	public void setCatAclView(String catAclView[]) {
		this.catAclView=catAclView;
	}

	/**
	 * Validate the properties.
	 */
	public ActionErrors validate(ActionMapping mapping,
		HttpServletRequest request) {

		ActionErrors errors = new ActionErrors();

		if(userId==null || userId.length()<1) {
			errors.add("userId",
				new ActionError("error.adminselectuserform.userId"));
		} else {
			try {
				Integer.parseInt(userId);
			} catch(NumberFormatException nfe) {
				errors.add("userId",
					new ActionError("error.adminselectuserform.userId.nfe"));
			}
		}

		return(errors);
	}

}

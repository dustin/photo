// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: AdminUserForm.java,v 1.4 2002/07/10 03:38:09 dustin Exp $

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
	private String password=null;
	private String realname=null;
	private String email=null;
	private boolean canadd=false;
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

	public String getPassword() {
		return(password);
	}

	public void setPassword(String password) {
		this.password=password;
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

	public boolean getCanadd() {
		return(canadd);
	}

	public String[] getCatAclAdd() {
		return(catAclAdd);
	}

	public String[] getCatAclView() {
		return(catAclView);
	}

	public void setCanadd(boolean canadd) {
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
				new ActionError("error.adminuserform.userId"));
		} else {
			try {
				Integer.parseInt(userId);
			} catch(NumberFormatException nfe) {
				errors.add("userId",
					new ActionError("error.adminuserform.userId.nfe"));
			}
		}

		// If the action is AdminSaveUserAction, we need to do more checks.
		if(mapping.getType().equals(AdminSaveUserAction.class.getName())) {
			if(username==null || username.length() < 1) {
				errors.add("username",
					new ActionError("error.adminuserform.username"));
			}
			if(password==null || password.length() < 1) {
				errors.add("password",
					new ActionError("error.adminuserform.password"));
			}
			if(realname==null || realname.length() < 1) {
				errors.add("username",
					new ActionError("error.adminuserform.realname"));
			}
			if(email==null || email.length() < 1) {
				errors.add("email",
					new ActionError("error.adminuserform.email"));
			} else {
				// This is cheap, but it's a start (7 == d@x.net)
				if(email.length() < 7 || (email.indexOf('@')==-1)) {
					errors.add("email",
						new ActionError("error.adminuserform.email.inv"));
				}
			}
			if(catAclView == null) {
				errors.add("catAclView",
					new ActionError("error.adminuserform.catAclView"));
			}
			if(catAclAdd == null) {
				errors.add("catAclAdd",
					new ActionError("error.adminuserform.catAclAdd"));
			}
		}

		return(errors);
	}

}

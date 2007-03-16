// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>

package net.spy.photo.struts;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;

/**
 * Form used by an admin to edit a user.
 */
public class AdminUserForm extends PhotoForm {

	private String userId=null;
	private String username=null;
	private String password=null;
	private String realname=null;
	private String email=null;
	private boolean canadd=false;
	private String catAclAdd[]=null;
	private String catAclView[]=null;
	private String adminStatus=null;

	/**
	 * Get the string representation of the user ID.
	 */
	public String getUserId() {
		return(userId);
	}

	/**
	 * Set the string representation of the user ID.
	 */
	public void setUserId(String to) {
		this.userId=to;
	}

	public void setUsername(String to) {
		this.username=to;
	}

	public String getUsername() {
		return(username);
	}

	public String getPassword() {
		return(password);
	}

	public void setPassword(String to) {
		this.password=to;
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

	public boolean getCanadd() {
		return(canadd);
	}

	public String[] getCatAclAdd() {
		return(catAclAdd);
	}

	public String[] getCatAclView() {
		return(catAclView);
	}

	public void setCanadd(boolean to) {
		this.canadd=to;
	}

	public void setCatAclAdd(String to[]) {
		this.catAclAdd=to;
	}

	public void setCatAclView(String to[]) {
		this.catAclView=to;
	}

	/**
	 * Get the admin type of this user.
	 */
	public String getAdminStatus() {
		return(adminStatus);
	}

	/**
	 * Set the admin type of this user.
	 */
	public void setAdminStatus(String to) {
		this.adminStatus=to;
	}

	/**
	 * Validate the properties.
	 */
	@Override
	public ActionErrors validate(ActionMapping mapping,
		HttpServletRequest request) {

		ActionErrors errors = new ActionErrors();

		if(userId==null || userId.length()<1) {
			errors.add("userId",
				new ActionMessage("error.adminuserform.userId"));
		} else {
			try {
				Integer.parseInt(userId);
			} catch(NumberFormatException nfe) {
				errors.add("userId",
					new ActionMessage("error.adminuserform.userId.nfe"));
			}
		}

		// If the action is AdminSaveUserAction, we need to do more checks.
		if(mapping.getType().equals(AdminSaveUserAction.class.getName())) {
			if(username==null || username.length() < 1) {
				errors.add("username",
					new ActionMessage("error.adminuserform.username"));
			}
			if(password==null || password.length() < 1) {
				errors.add("password",
					new ActionMessage("error.adminuserform.password"));
			}
			if(realname==null || realname.length() < 1) {
				errors.add("username",
					new ActionMessage("error.adminuserform.realname"));
			}
			if(email==null || email.length() < 1) {
				errors.add("email",
					new ActionMessage("error.adminuserform.email"));
			} else {
				// This is cheap, but it's a start (7 == d@x.net)
				if(email.length() < 7 || (email.indexOf('@')==-1)) {
					errors.add("email",
						new ActionMessage("error.adminuserform.email.inv"));
				}
			}
			if(catAclView == null) {
				errors.add("catAclView",
					new ActionMessage("error.adminuserform.catAclView"));
			}
			if(catAclAdd == null) {
				errors.add("catAclAdd",
					new ActionMessage("error.adminuserform.catAclAdd"));
			}
			if(adminStatus == null) {
				errors.add("adminStatus",
					new ActionMessage("error.adminuserform.adminStatus"));
			} else {
				// Check the actual values.
				if(adminStatus.equals("none")) {
					// OK
				} else if(adminStatus.equals("admin")) {
					// OK
				} else if(adminStatus.equals("subadmin")) {
					// OK
				} else {
					errors.add("adminStatus",
						new ActionMessage("error.adminuserform.adminStatus.inv"));
				}
			}
		}

		return(errors);
	}

	/** 
	 * Clear out the old stuff before adding new stuff.
	 * 
	 * @param mapping the servlet mapping being called
	 * @param request the request object
	 */
	@Override
	public void reset(ActionMapping mapping, HttpServletRequest request) {
		catAclAdd=new String[0];
		catAclView=new String[0];
	}

}

// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: AdminCategoryForm.java,v 1.1 2002/06/23 01:17:01 dustin Exp $

package net.spy.photo.struts;

import javax.servlet.http.HttpServletRequest;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

/**
 * Form used for editing categories.
 */
public class AdminCategoryForm extends ActionForm {

	private String catId=null;
	private String name=null;

	private String catAclAdd[]=null;
	private String catAclView[]=null;

	/**
	 * Get an instance of AdminCategoryForm.
	 */
	public AdminCategoryForm() {
		super();
	}

	public String getCatId() {
		return(catId);
	}

	public void setCatId(String catId) {
		this.catId=catId;
	}

	public String getName() {
		return(name);
	}

	public void setName(String name) {
		this.name=name;
	}

	public String[] getCatAclAdd() {
		return(catAclAdd);
	}

	public void setCatAclAdd(String[] catAclAdd) {
		this.catAclAdd=catAclAdd;
	}

	public String[] getCatAclView() {
		return(catAclView);
	}

	public void setCatAclView(String[] catAclView) {
		this.catAclView=catAclView;
	}

	/**
	 * Validate the properties.
	 */
	public ActionErrors validate(ActionMapping mapping,
		HttpServletRequest request) {

		ActionErrors errors = new ActionErrors();

		if(catId==null || catId.length() < 1) {
			errors.add("catId",
				new ActionError("error.admincatform.catId"));
		} else {
			try {
				Integer.parseInt(catId);
			} catch(NumberFormatException nfe) {
				errors.add("catId",
					new ActionError("error.admincatform.catId.nfe"));
			}
		}

		// If the action is AdminSaveCatAction, we need to do more checks
		if(mapping.getType().equals(AdminSaveCatAction.class.getName())) {
			if(name==null || name.length() < 1) {
				errors.add("name",
					new ActionError("error.admincatform.name"));
			}
			if(catAclAdd==null) {
				errors.add("catAclAdd",
					new ActionError("error.admincatform.catAclAdd"));
			}
			if(catAclView==null) {
				errors.add("catAclView",
					new ActionError("error.admincatform.catAclView"));
			}
		}

		return(errors);
	}

}

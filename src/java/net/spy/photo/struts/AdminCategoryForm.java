// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>

package net.spy.photo.struts;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;

/**
 * Form used for editing categories.
 */
public class AdminCategoryForm extends PhotoForm {

	private String catId=null;
	private String name=null;

	private String catAclAdd[]=null;
	private String catAclView[]=null;

	public String getCatId() {
		return(catId);
	}

	public void setCatId(String to) {
		this.catId=to;
	}

	public String getName() {
		return(name);
	}

	public void setName(String to) {
		this.name=to;
	}

	public String[] getCatAclAdd() {
		return(catAclAdd);
	}

	public void setCatAclAdd(String[] to) {
		this.catAclAdd=to;
	}

	public String[] getCatAclView() {
		return(catAclView);
	}

	public void setCatAclView(String[] to) {
		this.catAclView=to;
	}

	/**
	 * Validate the properties.
	 */
	@Override
	public ActionErrors validate(ActionMapping mapping,
		HttpServletRequest request) {

		ActionErrors errors = new ActionErrors();

		if(catId==null || catId.length() < 1) {
			errors.add("catId",
				new ActionMessage("error.admincatform.catId"));
		} else {
			try {
				Integer.parseInt(catId);
			} catch(NumberFormatException nfe) {
				errors.add("catId",
					new ActionMessage("error.admincatform.catId.nfe"));
			}
		}

		// If the action is AdminSaveCatAction, we need to do more checks
		if(mapping.getType().equals(AdminSaveCatAction.class.getName())) {
			if(name==null || name.length() < 1) {
				errors.add("name",
					new ActionMessage("error.admincatform.name"));
			}
			if(catAclAdd==null) {
				errors.add("catAclAdd",
					new ActionMessage("error.admincatform.catAclAdd"));
			}
			if(catAclView==null) {
				errors.add("catAclView",
					new ActionMessage("error.admincatform.catAclView"));
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

// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: AdminSaveCatAction.java,v 1.1 2002/06/23 01:17:01 dustin Exp $

package net.spy.photo.struts;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.apache.struts.action.*;

import net.spy.photo.*;

/**
 * Action used to save a category.
 */
public class AdminSaveCatAction extends AdminAction {

	/**
	 * Get an instance of AdminSaveCatAction.
	 */
	public AdminSaveCatAction() {
		super();
	}

	/**
	 * Perform the action.
	 */
	public ActionForward perform(ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,HttpServletResponse response)
		throws IOException, ServletException {

		// Verify the user is an admin
		checkAdmin(request);

		// Get the form
		AdminCategoryForm acf=(AdminCategoryForm)form;

		// Get the category
		Category cat=null;
		try {
			cat=Category.lookupCategory(Integer.parseInt(acf.getCatId()));
			cat.loadACLs();
		} catch(Exception e) {
			e.printStackTrace();
		}
		// If we didn't get a category, make a new one.
		if(cat==null) {
			cat=new Category();
		}

		// OK, now set the new stuff
		cat.setName(acf.getName());

		// Set the ACLs
		String acls[]=acf.getCatAclView();
		for(int i=0; i<acls.length; i++) {
			int uid=Integer.parseInt(acls[i]);
			cat.addViewACLEntry(uid);
		}
		acls=acf.getCatAclAdd();
		for(int i=0; i<acls.length; i++) {
			int uid=Integer.parseInt(acls[i]);
			cat.addAddACLEntry(uid);
		}

		try {
			cat.save();
		} catch(Exception e) {
			throw new ServletException("Error saving category", e);
		}

		return(mapping.findForward("success"));
	}

}

// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: AdminSaveCatAction.java,v 1.6 2003/01/09 07:42:55 dustin Exp $

package net.spy.photo.struts;

import java.io.IOException;

import javax.servlet.ServletException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import net.spy.db.Saver;

import net.spy.photo.Category;
import net.spy.photo.PhotoConfig;

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

		// Get rid of the old.
		cat.removeAllACLEntries();

		// Add the view entries
		String acls[]=acf.getCatAclView();
		for(int i=0; i<acls.length; i++) {
			int uid=Integer.parseInt(acls[i]);
			cat.addViewACLEntry(uid);
		}
		// Add the add entries
		acls=acf.getCatAclAdd();
		for(int i=0; i<acls.length; i++) {
			int uid=Integer.parseInt(acls[i]);
			cat.addAddACLEntry(uid);
		}

		try {
			Saver saver=new Saver(new PhotoConfig());
			saver.save(cat);
		} catch(Exception e) {
			throw new ServletException("Error saving category", e);
		}

		try {
			Category.recache();
		} catch(Exception e) {
			throw new ServletException("Error recaching categories.", e);
		}

		return(mapping.findForward("success"));
	}

}

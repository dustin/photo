// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: AdminSaveUserAction.java,v 1.3 2002/06/22 23:27:45 dustin Exp $

package net.spy.photo.struts;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.apache.struts.action.*;

import net.spy.photo.*;

/**
 * Action used to save a new user
 */
public class AdminSaveUserAction extends AdminAction {

	/**
	 * Get an instance of AdminSaveUserAction.
	 */
	public AdminSaveUserAction() {
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
		AdminUserForm auf=(AdminUserForm)form;

		// Get the security to get the user to populate to the fields to
		// save the world.
		PhotoSecurity security=null;
		try {
			security=new PhotoSecurity();
		} catch(Exception e) {
			throw new ServletException("Couldn't get security.", e);
		}

		// Get the user, or a new one if this a new user.
		PhotoUser user=security.getUser(Integer.parseInt(auf.getUserId()));
		if(user==null) {
			user=new PhotoUser();
		}

		user.setUsername(auf.getUsername());
		try {
			user.setPassword(auf.getPassword());
		} catch(Exception e) {
			throw new ServletException("Error setting password", e);
		}
		user.setRealname(auf.getRealname());
		user.setEmail(auf.getEmail());
		user.canAdd(auf.getCanadd());

		// Set the ACLs
		String acls[]=auf.getCatAclView();
		for(int i=0; i<acls.length; i++) {
			int cat=Integer.parseInt(acls[i]);
			user.addViewACLEntry(cat);
		}
		acls=auf.getCatAclAdd();
		for(int i=0; i<acls.length; i++) {
			int cat=Integer.parseInt(acls[i]);
			user.addAddACLEntry(cat);
		}

		try {
			user.save();
		} catch(Exception e) {
			throw new ServletException("Error saving user", e);
		}

		return(mapping.findForward("success"));
	}

}

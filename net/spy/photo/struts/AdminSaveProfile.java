// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: AdminSaveProfile.java,v 1.1 2002/06/23 01:38:47 dustin Exp $

package net.spy.photo.struts;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.apache.struts.action.*;

import net.spy.photo.*;

/**
 * Action used to save a new profile.
 */
public class AdminSaveProfile extends AdminAction {

	/**
	 * Get an instance of AdminSaveProfile.
	 */
	public AdminSaveProfile() {
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
		checkAdminOrSubadmin(request);

		// Get the form
		AdminNewProfileForm anpf=(AdminNewProfileForm)form;

		// Make a new profile
		Profile profile=new Profile();
		profile.setDescription(anpf.getName());
		String acls[]=anpf.getCategories();
		for(int i=0; i<acls.length; i++) {
			int cat=Integer.parseInt(acls[i]);
			profile.addACLEntry(cat);
		}

		// Save the profile
		try {
			profile.save();
		} catch(Exception e) {
			throw new ServletException("Error saving profile", e);
		}

		// Record the new profile identifier.
		request.setAttribute("net.spy.photo.ProfileId", profile.getName());

		return(mapping.findForward("success"));
	}

}

// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: AdminSaveProfile.java,v 1.6 2003/07/23 04:29:26 dustin Exp $

package net.spy.photo.struts;

import java.io.IOException;

import javax.servlet.ServletException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import net.spy.db.Saver;

import net.spy.photo.Profile;
import net.spy.photo.PhotoConfig;

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
	public ActionForward spyExecute(ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,HttpServletResponse response)
		throws Exception {

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
		Saver saver=new Saver(new PhotoConfig());
		saver.save(profile);

		// Record the new profile identifier.
		request.setAttribute("net.spy.photo.ProfileId", profile.getName());

		return(mapping.findForward("next"));
	}

}

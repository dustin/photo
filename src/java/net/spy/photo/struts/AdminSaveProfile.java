// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
// arch-tag: 0F598ADC-5D6E-11D9-8DAE-000A957659CC

package net.spy.photo.struts;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.spy.db.Saver;
import net.spy.photo.PhotoConfig;
import net.spy.photo.Profile;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * Action used to save a new profile.
 */
public class AdminSaveProfile extends PhotoAction {

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
		Saver saver=new Saver(PhotoConfig.getInstance());
		saver.save(profile);

		// Record the new profile identifier.
		request.setAttribute("net.spy.photo.ProfileId", profile.getName());

		return(mapping.findForward("next"));
	}

}

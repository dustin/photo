// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: NewUserAction.java,v 1.6 2003/01/07 09:38:53 dustin Exp $

package net.spy.photo.struts;

import java.io.IOException;

import java.sql.PreparedStatement;

import java.util.Iterator;

import javax.servlet.ServletException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.spy.SpyDB;
import net.spy.db.Saver;

import net.spy.photo.Persistent;
import net.spy.photo.PhotoConfig;
import net.spy.photo.PhotoSessionData;
import net.spy.photo.PhotoUser;
import net.spy.photo.PhotoUserException;
import net.spy.photo.NoSuchPhotoUserException;
import net.spy.photo.Profile;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * Create a user from a profile.
 */
public class NewUserAction extends PhotoAction {

	/**
	 * Get an instance of NewUserAction.
	 */
	public NewUserAction() {
		super();
	}

	/**
	 * Process the request.
	 */
	public ActionForward perform(ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,HttpServletResponse response)
		throws IOException, ServletException {

		NewUserForm nuf=(NewUserForm)form;

		// Get the profile
		Profile p=null;
		try {
			p=new Profile(nuf.getProfile());
		} catch(Exception e) {
			throw new ServletException("Error loading profile", e);
		}

		// Verify the user doesn't already exist.
		PhotoUser pu=null;
		try {
			pu=Persistent.getSecurity().getUser(nuf.getUsername());
		} catch(NoSuchPhotoUserException e) {
			// This is supposed to happen
		} catch(PhotoUserException e) {
			throw new ServletException("Error initializing new user.", e);
		}
		if(pu!=null) {
			throw new ServletException("User " + nuf.getUsername()
				+ " already exists.");
		}

		// Get the new user and fill it with the data from the form
		pu=new PhotoUser();
		pu.setUsername(nuf.getUsername());
		try {
			pu.setPassword(nuf.getPassword());
		} catch(Exception e) {
			throw new ServletException("Error setting password", e);
		}
		pu.setRealname(nuf.getRealname());
		pu.setEmail(nuf.getEmail());

		// Populate the ACL entries.
		for(Iterator it=p.getACLEntries().iterator(); it.hasNext();) {
			Integer i=(Integer)it.next();
			pu.addViewACLEntry(i.intValue());
		}

		// Save the new user
		try {
			Saver saver=new Saver(new PhotoConfig());
			saver.save(pu);
		} catch(Exception e) {
			throw new ServletException("Error saving user", e);
		}

		// Get the session data and assign the new credentials
		PhotoSessionData sessionData=getSessionData(request);
		sessionData.setUser(pu);

		// Try to log it.
		try {
			SpyDB db=new SpyDB(new PhotoConfig());
			PreparedStatement pst=db.prepareStatement(
				"insert into user_profile_log"
				+ "(profile_id, wwwuser_id, remote_addr) "
				+ "values(?,?,?)"
				);
			pst.setInt(1, p.getId());
			pst.setInt(2, pu.getId());
			pst.setString(3, request.getRemoteAddr());
			pst.executeUpdate();
			pst.close();
			db.close();
		} catch(Exception e) {
			e.printStackTrace();
		}

		return(mapping.findForward("success"));
	}

}

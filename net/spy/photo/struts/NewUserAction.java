// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: NewUserAction.java,v 1.2 2002/06/14 18:27:24 dustin Exp $

package net.spy.photo.struts;

import java.io.*; 
import java.util.*;
import java.sql.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.apache.struts.action.*;

import net.spy.SpyDB;

import net.spy.photo.*;

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
		PhotoUser pu=Persistent.getSecurity().getUser(nuf.getUsername());
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
		for(Enumeration e=p.getACLEntries(); e.hasMoreElements();) {
			Integer i=(Integer)e.nextElement();
			pu.addViewACLEntry(i.intValue());
		}

		// Save the new user
		try {
			pu.save();
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

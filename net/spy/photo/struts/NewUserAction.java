// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: NewUserAction.java,v 1.10 2003/07/14 06:21:28 dustin Exp $

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
import org.apache.struts.validator.DynaValidatorForm;

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
	public ActionForward execute(ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,HttpServletResponse response)
		throws Exception {

		DynaValidatorForm nuf=(DynaValidatorForm)form;

		// Get the profile
		Profile p=new Profile((String)nuf.get("profile"));

		// Verify the user doesn't already exist.
		PhotoUser pu=null;
		String username=(String)nuf.get("username");
		try {
			pu=Persistent.getSecurity().getUser(username);
		} catch(NoSuchPhotoUserException e) {
			// This is supposed to happen
		}
		if(pu!=null) {
			throw new ServletException("User " + username + " already exists.");
		}

		// Get the new user and fill it with the data from the form
		pu=new PhotoUser();
		pu.setUsername(username);
		pu.setPassword((String)nuf.get("password"));
		pu.setRealname((String)nuf.get("realname"));
		pu.setEmail((String)nuf.get("email"));

		// Populate the ACL entries.
		for(Iterator it=p.getACLEntries().iterator(); it.hasNext();) {
			Integer i=(Integer)it.next();
			pu.addViewACLEntry(i.intValue());
		}

		// Save the new user
		Saver saver=new Saver(new PhotoConfig());
		saver.save(pu);

		// Recache the photo stuff and get the cached version.
		PhotoUser.recache();
		// Get the user from the cache
		pu=PhotoUser.getPhotoUser(username);

		// Get the session data and assign the new credentials
		PhotoSessionData sessionData=getSessionData(request);
		sessionData.setUser(pu);

		// Try to log it.
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

		return(mapping.findForward("next"));
	}

}

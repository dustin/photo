// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: AdminSaveUserAction.java,v 1.10 2003/01/09 07:42:55 dustin Exp $

package net.spy.photo.struts;

import java.io.IOException;
import java.sql.PreparedStatement;

import javax.servlet.ServletException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import net.spy.SpyDB;
import net.spy.db.Saver;

import net.spy.photo.PhotoConfig;
import net.spy.photo.PhotoSecurity;
import net.spy.photo.PhotoUser;
import net.spy.photo.PhotoUserException;

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
		PhotoSecurity security=new PhotoSecurity();

		// Get the user, or a new one if this a new user.
		PhotoUser user=null;
		try {
			user=security.getUser(Integer.parseInt(auf.getUserId()));
		} catch(PhotoUserException e) {
			e.printStackTrace();
		}
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

		// First, remove all of the ACL entries
		user.removeAllACLEntries();

		// Now add back the view entries
		String acls[]=auf.getCatAclView();
		for(int i=0; i<acls.length; i++) {
			int cat=Integer.parseInt(acls[i]);
			System.err.println("Adding view entry for " + cat);
			user.addViewACLEntry(cat);
		}
		// Followed by the add entries
		acls=auf.getCatAclAdd();
		for(int i=0; i<acls.length; i++) {
			int cat=Integer.parseInt(acls[i]);
			System.err.println("Adding add entry for " + cat);
			user.addAddACLEntry(cat);
		}

		try {
			Saver saver=new Saver(new PhotoConfig());
			saver.save(user);
		} catch(Exception e) {
			throw new ServletException("Error saving user", e);
		}

		// Recache the users.
		try {
			PhotoUser.recache();
		} catch(PhotoUserException e) {
			throw new ServletException("Error recaching users.", e);
		}

		// Update the groups...yeah, I guess I'm just going to do it right
		// here for now.
		try {
			// I won't even bother doing this in a transaction, worse case,
			// user doesn't get permissions and we get an exception.
			SpyDB db=new SpyDB(new PhotoConfig());

			// Delete any old group relations for this user
			PreparedStatement pst=db.prepareStatement(
				"delete from wwwgroup where userid=?");
			pst.setInt(1, user.getId());
			pst.executeUpdate();
			pst.close();

			// Add the user to a new group if the admin status is not none
			if(! auf.getAdminStatus().equals("none")) {
				pst=db.prepareStatement(
					"insert into wwwgroup(userid, groupname) values(?,?)");
				pst.setInt(1, user.getId());
				pst.setString(2, auf.getAdminStatus());
				pst.executeUpdate();
				pst.close();
			}

			// Done
			db.close();
		} catch(Exception e) {
			throw new ServletException("Error saving wwwgroup", e);
		}

		return(mapping.findForward("success"));
	}

}

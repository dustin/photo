// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
// arch-tag: 119E925B-5D6E-11D9-A410-000A957659CC

package net.spy.photo.struts;

import java.io.IOException;
import java.sql.PreparedStatement;

import javax.servlet.ServletException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import net.spy.db.SpyDB;
import net.spy.db.Saver;

import net.spy.photo.PhotoConfig;
import net.spy.photo.PhotoSecurity;
import net.spy.photo.PhotoUser;
import net.spy.photo.PhotoUserException;

/**
 * Action used to save a new user
 */
public class AdminSaveUserAction extends PhotoAction {

	/**
	 * Get an instance of AdminSaveUserAction.
	 */
	public AdminSaveUserAction() {
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
		user.setPassword(auf.getPassword());
		user.setRealname(auf.getRealname());
		user.setEmail(auf.getEmail());
		user.canAdd(auf.getCanadd());

		// Set the ACLs

		// First, remove all of the ACL entries
		user.getACL().removeAllEntries();

		// Now add back the view entries
		String acls[]=auf.getCatAclView();
		for(int i=0; i<acls.length; i++) {
			int cat=Integer.parseInt(acls[i]);
			System.err.println("Adding view entry for " + cat);
			user.getACL().addViewEntry(cat);
		}
		// Followed by the add entries
		acls=auf.getCatAclAdd();
		for(int i=0; i<acls.length; i++) {
			int cat=Integer.parseInt(acls[i]);
			System.err.println("Adding add entry for " + cat);
			user.getACL().addAddEntry(cat);
		}

		Saver saver=new Saver(PhotoConfig.getInstance());
		saver.save(user);

		// Recache the users.
		PhotoUser.recache();

		// Update the groups...yeah, I guess I'm just going to do it right
		// here for now.
		// I won't even bother doing this in a transaction, worse case,
		// user doesn't get permissions and we get an exception.
		SpyDB db=new SpyDB(PhotoConfig.getInstance());

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

		return(mapping.findForward("next"));
	}

}

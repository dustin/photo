// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
// arch-tag: 119E925B-5D6E-11D9-A410-000A957659CC

package net.spy.photo.struts;

import java.sql.PreparedStatement;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.spy.db.SpyDB;
import net.spy.photo.MutableUser;
import net.spy.photo.NoSuchPhotoUserException;
import net.spy.photo.PhotoConfig;
import net.spy.photo.UserFactory;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * Action used to save a new user
 */
public class AdminSaveUserAction extends PhotoAction {

	/**
	 * Perform the action.
	 */
	public ActionForward spyExecute(ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,HttpServletResponse response)
		throws Exception {

		// Get the form
		AdminUserForm auf=(AdminUserForm)form;

		UserFactory uf=UserFactory.getInstance();

		// Get the user, or a new one if this a new user.
		MutableUser user=null;
		try {
			user=uf.getMutable(Integer.parseInt(auf.getUserId()));
		} catch(NoSuchPhotoUserException e) {
			getLogger().debug("This must be a new user", e);
		}
		if(user==null) {
			user=uf.createNew();
		}

		user.setName(auf.getUsername());
		user.setPassword(auf.getPassword());
		user.setRealname(auf.getRealname());
		user.setEmail(auf.getEmail());
		user.setCanAdd(auf.getCanadd());

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

		uf.persist(user);

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

// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>

package net.spy.photo.struts;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import net.spy.db.SpyDB;
import net.spy.photo.PhotoACLEntry;
import net.spy.photo.PhotoConfig;
import net.spy.photo.PhotoSecurity;
import net.spy.photo.User;

/**
 * Action used to begin editing a new user.
 */
public class AdminSelectUserAction extends PhotoAction {

	/**
	 * Perform the action.
	 */
	@Override
	public ActionForward spyExecute(ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,HttpServletResponse response)
		throws Exception {

		// Get the form
		AdminUserForm auf=(AdminUserForm)form;

		// Get the user id from the form
		int userid=Integer.parseInt(auf.getUserId());

		// The form can remain blank if the user id is -1 (new user)
		if(userid==-1) {
			// Empty user, fill it out with some defaults
			auf.setUsername("newuser");
			auf.setPassword("");
			auf.setRealname("");
			auf.setEmail("");
			auf.setCanadd(false);
			auf.setCatAclAdd(new String[0]);
			auf.setCatAclView(new String[0]);
		} else {
			// Look up the user
			PhotoSecurity sec=new PhotoSecurity();
			User user=sec.getUser(userid);

			// Set the easy stuff
			auf.setUsername(user.getName());
			auf.setPassword(user.getPassword());
			auf.setRealname(user.getRealname());
			auf.setEmail(user.getEmail());
			auf.setCanadd(user.canAdd());

			// Populate the ACL stuff
			ArrayList<String> viewable=new ArrayList<String>();
			ArrayList<String> addable=new ArrayList<String>();
			for(PhotoACLEntry acl : user.getACL()) {
				int id=acl.getWhat();
				if(acl.canAdd()) {
					addable.add(String.valueOf(id));
				}
				if(acl.canView()) {
					viewable.add(String.valueOf(id));
				}
			}

			// Store them
			auf.setCatAclAdd(addable.toArray(new String[0]));
			auf.setCatAclView(viewable.toArray(new String[0]));

			// Look up the group thingy.
			SpyDB db=new SpyDB(PhotoConfig.getInstance());
			PreparedStatement pst=db.prepareStatement(
				"select groupname from wwwgroup where userid=?");
			pst.setInt(1, user.getId());
			ResultSet rs=pst.executeQuery();
			if(rs.next()) {
				// If there's a group name, get it
				String groupName=rs.getString("groupname");
				auf.setAdminStatus(groupName);
			} else {
				// If not, set it to none
				auf.setAdminStatus("none");
			}
			if(rs.next()) {
				throw new ServletException(
					"Too many results returned for group lookup, "
						+ "I'm confused");
			}
			rs.close();
			pst.close();
			db.close();
		}

		return(mapping.findForward("next"));
	}

}

// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: AdminSelectUserAction.java,v 1.13 2003/07/23 04:29:26 dustin Exp $

package net.spy.photo.struts;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Iterator;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
import net.spy.photo.PhotoUser;
import net.spy.photo.PhotoUserException;

/**
 * Action used to begin editing a new user.
 */
public class AdminSelectUserAction extends PhotoAction {

	/**
	 * Get an instance of AdminSelectUserAction.
	 */
	public AdminSelectUserAction() {
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
			PhotoUser user=sec.getUser(userid);

			// Set the easy stuff
			auf.setUsername(user.getUsername());
			auf.setPassword(user.getPassword());
			auf.setRealname(user.getRealname());
			auf.setEmail(user.getEmail());
			auf.setCanadd(user.canAdd());

			// Populate the ACL stuff
			ArrayList viewable=new ArrayList();
			ArrayList addable=new ArrayList();
			for(Iterator i=user.getACLEntries().iterator(); i.hasNext();) {
				PhotoACLEntry acl=(PhotoACLEntry)i.next();

				int id=acl.getCat();
				if(acl.canAdd()) {
					addable.add("" + id);
				}
				if(acl.canView()) {
					viewable.add("" + id);
				}
			}

			// Store them
			auf.setCatAclAdd((String[])addable.toArray(new String[0]));
			auf.setCatAclView((String[])viewable.toArray(new String[0]));

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

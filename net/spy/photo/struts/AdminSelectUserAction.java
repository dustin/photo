// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: AdminSelectUserAction.java,v 1.2 2002/06/22 21:09:01 dustin Exp $

package net.spy.photo.struts;

import java.io.*;
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.apache.struts.action.*;

import net.spy.photo.*;

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
	public ActionForward perform(ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,HttpServletResponse response)
		throws IOException, ServletException {

		AdminUserForm auf=(AdminUserForm)form;

		// Get the user id from the form
		int userid=Integer.parseInt(auf.getUserId());

		// The form can remain blank if the user id is -1 (new user)
		if(userid!=-1) {
			// Look up the user
			PhotoSecurity sec=null;
			try {
				sec=new PhotoSecurity();
			} catch(Exception e) {
				throw new ServletException("Couldn't get security", e);
			}
			PhotoUser user=sec.getUser(userid);

			if(user==null) {
				throw new ServletException("No such user:  " + userid);
			}

			// Set the easy stuff
			auf.setUsername(user.getUsername());
			auf.setRealname(user.getRealname());
			auf.setEmail(user.getEmail());
			if(user.canAdd()) {
				auf.setCanadd("1");
			}

			// Populate the ACL stuff
			Vector viewable=new Vector();
			Vector addable=new Vector();
			for(Enumeration e=user.getACLEntries(); e.hasMoreElements();) {
				PhotoACLEntry acl=(PhotoACLEntry)e.nextElement();

				int id=acl.getCat();
				if(acl.canAdd()) {
					addable.addElement("" + id);
				}
				if(acl.canView()) {
					viewable.addElement("" + id);
				}
			}

			// Store them
			auf.setCatAclAdd((String[])addable.toArray(new String[0]));
			auf.setCatAclView((String[])viewable.toArray(new String[0]));
		}

		return(mapping.findForward("success"));
	}

}

// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: AdminSelectCatAction.java,v 1.1 2002/06/23 01:17:01 dustin Exp $

package net.spy.photo.struts;

import java.io.*;
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.apache.struts.action.*;

import net.spy.photo.*;

/**
 * Action used to begin editing a category.
 */
public class AdminSelectCatAction extends AdminAction {

	/**
	 * Get an instance of AdminSelectCatAction.
	 */
	public AdminSelectCatAction() {
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
		AdminCategoryForm acf=(AdminCategoryForm)form;

		// Get the user id from the form
		int catid=Integer.parseInt(acf.getCatId());

		// The form can remain blank if the user cat is -1 (new cat)
		if(catid==-1) {
			// Empty user, fill it out with some defaults
			acf.setName("New Category");
			acf.setCatAclAdd(new String[0]);
			acf.setCatAclView(new String[0]);
		} else {
			// Look up the user
			Category cat=null;
			try {
				cat=Category.lookupCategory(Integer.parseInt(acf.getCatId()));
				cat.loadACLs();
			} catch(Exception e) {
				throw new ServletException("Couldn't lookup category", e);
			}

			// Set the easy stuff
			acf.setName(cat.getName());

			// Populate the ACL stuff
			Vector viewable=new Vector();
			Vector addable=new Vector();
			for(Enumeration e=cat.getACLEntries(); e.hasMoreElements();) {
				PhotoACLEntry acl=(PhotoACLEntry)e.nextElement();

				int id=acl.getUid();
				if(acl.canAdd()) {
					addable.addElement("" + id);
				}
				if(acl.canView()) {
					viewable.addElement("" + id);
				}
			}

			// Store them
			acf.setCatAclAdd((String[])addable.toArray(new String[0]));
			acf.setCatAclView((String[])viewable.toArray(new String[0]));
		}

		return(mapping.findForward("success"));
	}

}

// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
// arch-tag: 12BF2BB0-5D6E-11D9-8FCE-000A957659CC

package net.spy.photo.struts;

import java.util.ArrayList;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.spy.photo.Category;
import net.spy.photo.CategoryFactory;
import net.spy.photo.PhotoACLEntry;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * Action used to begin editing a category.
 */
public class AdminSelectCatAction extends PhotoAction {

	/**
	 * Get an instance of AdminSelectCatAction.
	 */
	public AdminSelectCatAction() {
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
			CategoryFactory cf=CategoryFactory.getInstance();
			Category cat=cf.getObject(Integer.parseInt(acf.getCatId()));

			// Set the easy stuff
			acf.setName(cat.getName());

			// Populate the ACL stuff
			ArrayList<String> viewable=new ArrayList<String>();
			ArrayList<String> addable=new ArrayList<String>();
			for(Iterator i=cat.getACL().iterator(); i.hasNext();) {
				PhotoACLEntry acl=(PhotoACLEntry)i.next();

				int id=acl.getWhat();
				if(acl.canAdd()) {
					addable.add(String.valueOf(id));
				}
				if(acl.canView()) {
					viewable.add(String.valueOf(id));
				}
			}

			// Store them
			acf.setCatAclAdd(addable.toArray(new String[0]));
			acf.setCatAclView(viewable.toArray(new String[0]));
		}

		return(mapping.findForward("next"));
	}

}

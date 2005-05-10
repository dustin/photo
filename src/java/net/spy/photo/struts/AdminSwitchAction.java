// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
// arch-tag: 14D9C494-5D6E-11D9-8FDF-000A957659CC

package net.spy.photo.struts;

import java.io.IOException;

import javax.servlet.ServletException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.spy.photo.PhotoSessionData;
import net.spy.photo.User;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * This action operates as a content switch, directing the user to
 * different content based on admin status.
 */
public class AdminSwitchAction extends PhotoAction {

	/**
	 * Get an instance of AdminSwitchAction.
	 */
	public AdminSwitchAction() {
		super();
	}

	/**
	 * Perform the action.
	 */
	public ActionForward spyExecute(ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,HttpServletResponse response)
		throws Exception {

		// Return value.
		ActionForward rv=null;

		// These are the possible forwards that may be passed in.
		ActionForward adminForward=mapping.findForward("admin");
		ActionForward subAdminForward=mapping.findForward("subadmin");
		ActionForward noAdminForward=mapping.findForward("noadmin");

		// Supply reasonable defaults.
		if(subAdminForward==null) {
			subAdminForward=noAdminForward;
		}
		if(adminForward==null) {
			adminForward=subAdminForward;
		}

		// OK, now figure out what the person is doing.
		PhotoSessionData sessionData=getSessionData(request);
		if(sessionData.checkAdminFlag(PhotoSessionData.ADMIN)) {
			rv=adminForward;
		} else if(sessionData.checkAdminFlag(PhotoSessionData.SUBADMIN)) {
			rv=subAdminForward;
		} else {
			rv=noAdminForward;
		}

		// Make sure we found something.
		if(rv==null) {
			throw new ServletException("No forward found for user.");
		}

		return(rv);
	}

}

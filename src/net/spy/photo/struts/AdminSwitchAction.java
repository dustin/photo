// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: AdminSwitchAction.java,v 1.3 2003/07/23 04:29:26 dustin Exp $

package net.spy.photo.struts;

import java.io.IOException;

import javax.servlet.ServletException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.spy.photo.PhotoSessionData;
import net.spy.photo.PhotoUser;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * This action operates as a content switch, directing the user to
 * different content based on admin status.
 */
public class AdminSwitchAction extends AdminAction {

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
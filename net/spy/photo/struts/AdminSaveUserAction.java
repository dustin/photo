// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: AdminSaveUserAction.java,v 1.1 2002/06/22 21:09:01 dustin Exp $

package net.spy.photo.struts;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.apache.struts.action.*;

import net.spy.photo.*;

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
	public ActionForward perform(ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,HttpServletResponse response)
		throws IOException, ServletException {

		AdminUserForm auf=(AdminUserForm)form;

		if(true) {
			throw new ServletException("NOT IMPLEMENTED");
		}

		return(mapping.findForward("success"));
	}

}

// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: AdminSelectUserAction.java,v 1.1 2002/06/22 08:27:07 dustin Exp $

package net.spy.photo.struts;

import java.io.*;

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

		// XXX:  Fill the form and forward to the edit form

		if(true) {
			throw new ServletException("Not implemented");
		}

		return(mapping.findForward("success"));
	}

}

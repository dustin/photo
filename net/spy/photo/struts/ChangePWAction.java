// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: ChangePWAction.java,v 1.1 2002/06/17 05:50:20 dustin Exp $

package net.spy.photo.struts;

import java.io.*;
import java.util.*;
import java.sql.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.apache.struts.action.*;

import net.spy.SpyDB;

import net.spy.photo.*;

/**
 * Action that changes user password.
 */
public class ChangePWAction extends PhotoAction {

	/**
	 * Get an instance of ChangePWAction.
	 */
	public ChangePWAction() {
		super();
	}

	/**
	 * Process the change password request.
	 */
	public ActionForward perform(ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,HttpServletResponse response)
		throws IOException, ServletException {

		ChangePWForm cpf=(ChangePWForm)form;

		PhotoSessionData sessionData=getSessionData(request);
		PhotoUser user=sessionData.getUser();

		if(!user.checkPassword(cpf.getOldpw())) {
			throw new ServletException("Invalid old password.");
		}

		// Set the password
		try {
			user.setPassword(cpf.getNewpw1());
			user.save();
		} catch(Exception e) {
			throw new ServletException("Error setting/save new password", e);
		}

		return(mapping.findForward("success"));
	}

}

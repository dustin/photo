// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: ForgotPWAction.java,v 1.1 2002/06/17 06:54:04 dustin Exp $

package net.spy.photo.struts;

import java.io.*;
import java.util.*;
import java.sql.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.apache.struts.action.*;

import net.spy.SpyDB;
import net.spy.util.PwGen;

import net.spy.photo.*;

/**
 * Action that changes user password.
 */
public class ForgotPWAction extends PhotoAction {

	/**
	 * Get an instance of ForgotPWAction.
	 */
	public ForgotPWAction() {
		super();
	}

	/**
	 * Process the forgotten password request.
	 */
	public ActionForward perform(ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,HttpServletResponse response)
		throws IOException, ServletException {

		ForgotPWForm fpf=(ForgotPWForm)form;

		// Get the user
		PhotoUser user=Persistent.getSecurity().getUser(fpf.getUsername());
		if(user==null) {
			throw new ServletException("No user matching " + fpf.getUsername());
		}

		try {
			String newPass=PwGen.getPass(8);
			user.setPassword(newPass);
			user.save();

			Mailer m=new Mailer();
			m.setTo(user.getEmail());
			m.setSubject("New Password for Photo Album");
			m.setBody("\n\nYour new password for " + user.getUsername()
				+ " is " + newPass + "\n\n");
			m.send();

			log("Emailed new password to " + user.getUsername());

		} catch(Exception e) {
			throw new ServletException("Error setting new password", e);
		}

		request.setAttribute("net.spy.photo.ForgottenUser", user);

		return(mapping.findForward("success"));
	}

}

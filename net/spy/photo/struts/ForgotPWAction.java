// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: ForgotPWAction.java,v 1.3 2002/12/15 09:02:25 dustin Exp $

package net.spy.photo.struts;

import java.io.IOException;

import javax.servlet.ServletException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.spy.db.Saver;

import net.spy.photo.PhotoConfig;
import net.spy.photo.Mailer;
import net.spy.photo.Persistent;
import net.spy.photo.PhotoUser;

import net.spy.util.PwGen;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

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
			Saver saver=new Saver(new PhotoConfig());
			saver.save(user);

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

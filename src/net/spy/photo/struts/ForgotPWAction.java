// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
// arch-tag: 2056392A-5D6E-11D9-9250-000A957659CC

package net.spy.photo.struts;

import java.io.IOException;

import javax.servlet.ServletException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.spy.db.Saver;

import net.spy.photo.PhotoConfig;
import net.spy.photo.Mailer;
import net.spy.photo.Persistent;
import net.spy.photo.User;
import net.spy.photo.impl.DBUser;

import net.spy.util.PwGen;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.DynaActionForm;

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
	public ActionForward spyExecute(ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,HttpServletResponse response)
		throws IOException, ServletException {

		DynaActionForm fpf=(DynaActionForm)form;

		// Get the user
		DBUser user=null;

		try {
			// Look up the user
			user=(DBUser)Persistent.getSecurity().getUser(
				(String)fpf.get("username"));

			// Verify the user doesn't end up being guest.
			if(user.getName().equals("guest")) {
				throw new ServletException("Can't set a password for guest");
			}

			String newPass=PwGen.getPass(8);
			user.setPassword(newPass);
			Saver saver=new Saver(PhotoConfig.getInstance());
			saver.save(user);

			Mailer m=new Mailer();
			m.setTo(user.getEmail());
			m.setSubject("New Password for Photo Album");
			m.setBody("\n\nYour new password for " + user.getName()
				+ " is " + newPass + "\n\n");
			m.send();

			getLogger().info("Emailed new password to " + user.getName());

		} catch(Exception e) {
			throw new ServletException("Error setting new password", e);
		}

		request.setAttribute("net.spy.photo.ForgottenUser", user);

		return(mapping.findForward("next"));
	}

}

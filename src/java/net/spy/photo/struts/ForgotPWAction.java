// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
// arch-tag: 2056392A-5D6E-11D9-9250-000A957659CC

package net.spy.photo.struts;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.spy.photo.Mailer;
import net.spy.photo.MutableUser;
import net.spy.photo.User;
import net.spy.photo.UserFactory;
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
		throws Exception {

		DynaActionForm fpf=(DynaActionForm)form;

		UserFactory uf=UserFactory.getInstance();

		// Look up the user
		User ur=uf.getUser((String)fpf.get("username"));

		// Verify the user doesn't end up being guest.
		if(ur.getName().equals("guest")) {
			throw new ServletException("Can't set a password for guest");
		}

		// Get the mutable user
		MutableUser user=uf.getMutable(ur.getId());

		String newPass=PwGen.getPass(8);
		user.setPassword(newPass);

		uf.persist(user);

		Mailer m=new Mailer();
		m.setRecipient(user.getEmail());
		m.setSubject("New Password for Photo Album");
		m.setBody("\n\nYour new password for " + user.getName()
			+ " is " + newPass + "\n\n");
		m.send();

		getLogger().info("Emailed new password to " + user.getName());

		request.setAttribute("net.spy.photo.ForgottenUser", user);

		return(mapping.findForward("next"));
	}

}

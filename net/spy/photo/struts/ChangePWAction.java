// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: ChangePWAction.java,v 1.2 2002/07/10 03:38:09 dustin Exp $

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

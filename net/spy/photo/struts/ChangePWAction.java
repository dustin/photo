// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: ChangePWAction.java,v 1.4 2003/05/25 08:17:41 dustin Exp $

package net.spy.photo.struts;

import java.io.IOException;

import javax.servlet.ServletException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.spy.db.Saver;

import net.spy.photo.PhotoConfig;
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
	public ActionForward execute(ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,HttpServletResponse response)
		throws Exception {

		ChangePWForm cpf=(ChangePWForm)form;

		PhotoSessionData sessionData=getSessionData(request);
		PhotoUser user=sessionData.getUser();

		if(!user.checkPassword(cpf.getOldpw())) {
			throw new ServletException("Invalid old password.");
		}

		// Set the password
		user.setPassword(cpf.getNewpw1());
		Saver saver=new Saver(new PhotoConfig());
		saver.save(user);

		return(mapping.findForward("success"));
	}

}

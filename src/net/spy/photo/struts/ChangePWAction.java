// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: ChangePWAction.java,v 1.7 2003/07/23 04:29:26 dustin Exp $

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
import org.apache.struts.action.DynaActionForm;

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
	public ActionForward spyExecute(ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,HttpServletResponse response)
		throws Exception {

		DynaActionForm cpf=(DynaActionForm)form;

		PhotoSessionData sessionData=getSessionData(request);
		PhotoUser user=sessionData.getUser();

		String oldpw=(String)cpf.get("oldpw");
		String newpw=(String)cpf.get("newpw1");
		if(!user.checkPassword(oldpw)) {
			throw new ServletException("Invalid old password.");
		}

		// Set the password
		user.setPassword(newpw);
		Saver saver=new Saver(PhotoConfig.getInstance());
		saver.save(user);

		return(mapping.findForward("next"));
	}

}

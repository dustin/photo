// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
// arch-tag: 18C01008-5D6E-11D9-A802-000A957659CC

package net.spy.photo.struts;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.spy.photo.MutableUser;
import net.spy.photo.Persistent;
import net.spy.photo.PhotoSecurity;
import net.spy.photo.PhotoSessionData;
import net.spy.photo.UserFactory;

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
	@Override
	public ActionForward spyExecute(ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,HttpServletResponse response)
		throws Exception {

		DynaActionForm cpf=(DynaActionForm)form;

		PhotoSessionData sessionData=getSessionData(request);
		UserFactory uf=UserFactory.getInstance();
		MutableUser user=uf.getMutable(sessionData.getUser().getId());

		String oldpw=(String)cpf.get("oldpw");
		String newpw=(String)cpf.get("newpw1");
		PhotoSecurity sec=Persistent.getSecurity();
		if(!sec.checkPassword(oldpw, user.getPassword())) {
			throw new ServletException("Invalid old password.");
		}

		// Set the password
		user.setPassword(newpw);
		uf.persist(user);

		addMessage(request, MessageType.success,
				"Your password has been changed");

		return(mapping.findForward("next"));
	}

}

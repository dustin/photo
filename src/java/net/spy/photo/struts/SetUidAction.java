// Copyright (c) 2004  2Wire, Inc.
// arch-tag: D2264AC5-7808-11D9-87B0-000A957659CC

package net.spy.photo.struts;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.spy.photo.Persistent;
import net.spy.photo.PhotoSessionData;
import net.spy.photo.User;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.DynaActionForm;

/**
 * Struts action for a user with admin role to become another user.
 */
public class SetUidAction extends PhotoAction {

	/**
	 * Get an instance of SetUidAction.
	 */
	public SetUidAction() {
		super();
	}

	/** 
	 * Perform this action.
	 */
	@Override
	public ActionForward spyExecute(ActionMapping mapping, ActionForm form,
		HttpServletRequest request, HttpServletResponse response)
		throws Exception {

		PhotoSessionData sessionData=getSessionData(request);
		User thisUser=sessionData.getUser();
		if(!thisUser.isInRole("admin")) {
			throw new ServletException("You are not an admin");
		}

		DynaActionForm lf=(DynaActionForm)form;

		User thatUser=Persistent.getSecurity().getUser((String)lf.get("user"));
		sessionData.setUser(thatUser);
		getLogger().info("User " + thisUser + " has become " + thatUser);

		addMessage(request, MessageType.success,
				"Set current user to " + thatUser.getRealname());

		return(mapping.findForward("next"));
	}

}

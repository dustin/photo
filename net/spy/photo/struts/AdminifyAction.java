// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: AdminifyAction.java,v 1.4 2003/05/26 08:02:52 dustin Exp $

package net.spy.photo.struts;

import java.io.IOException;

import javax.servlet.ServletException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.spy.photo.PhotoException;
import net.spy.photo.PhotoSessionData;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.DynaActionForm;

/**
 * Action for setting admin status.
 */
public class AdminifyAction extends PhotoAction {

	/**
	 * Get an instance of AdminifyAction.
	 */
	public AdminifyAction() {
		super();
	}

	/**
	 * Enable or disable administrative status.
	 */
	public ActionForward execute(ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,HttpServletResponse response)
		throws Exception {

		ActionForward rv=null;

		DynaActionForm af=(DynaActionForm)form;

		PhotoSessionData sessionData=getSessionData(request);

		String action=(String)af.get("action");
		if(action.equals("setadmin")) {
			sessionData.setAdmin();
		} else if(action.equals("unsetadmin")) {
			sessionData.unSetAdmin();
		} else {
			throw new ServletException("Invalid adminify action:  " + action);
		}

		rv=mapping.findForward("success");

		return(rv);
	}

}

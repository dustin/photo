// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: AdminifyAction.java,v 1.3 2003/05/25 08:17:41 dustin Exp $

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

		AdminifyForm af=(AdminifyForm)form;

		PhotoSessionData sessionData=getSessionData(request);

		if(af.getAction().equals("setadmin")) {
			try {
				sessionData.setAdmin();
			} catch(PhotoException pe) {
				throw new ServletException("Error setting admin", pe);
			}
		} else if(af.getAction().equals("unsetadmin")) {
			sessionData.unSetAdmin();
		} else {
			throw new ServletException("Invalid adminify action:  "
				+ af.getAction());
		}

		rv=mapping.findForward("success");

		return(rv);
	}

}

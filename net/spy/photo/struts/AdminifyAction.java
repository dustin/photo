// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: AdminifyAction.java,v 1.1 2002/05/28 06:37:25 dustin Exp $

package net.spy.photo.struts;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.apache.struts.action.*;

import net.spy.photo.*;

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
	public ActionForward perform(ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,HttpServletResponse response)
		throws IOException, ServletException {

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

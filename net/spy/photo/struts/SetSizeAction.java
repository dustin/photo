// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: SetSizeAction.java,v 1.1 2002/06/15 07:20:30 dustin Exp $

package net.spy.photo.struts;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.apache.struts.action.*;

import net.spy.photo.*;

/**
 * Action to set the size of an image.
 */
public class SetSizeAction extends PhotoAction {

	/**
	 * Get an instance of SetSizeAction.
	 */
	public SetSizeAction() {
		super();
	}

	/**
	 * Set the optimal viewing size.
	 */
	public ActionForward perform(ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,HttpServletResponse response)
		throws IOException, ServletException {

		SetSizeForm ssf=(SetSizeForm)form;

		PhotoSessionData sessionData=getSessionData(request);
		sessionData.setOptimalDimensions(
			new PhotoDimensionsImpl(ssf.getDims()));

		System.out.println("Set viewing size to " + ssf.getDims());

		return(mapping.findForward("success"));
	}

}

// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: SetSizeAction.java,v 1.2 2002/07/10 03:38:09 dustin Exp $

package net.spy.photo.struts;

import java.io.IOException;

import javax.servlet.ServletException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.spy.photo.PhotoDimensionsImpl;
import net.spy.photo.PhotoSessionData;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

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

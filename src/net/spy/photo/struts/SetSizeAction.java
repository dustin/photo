// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
// arch-tag: 3CBE8084-5D6E-11D9-AD47-000A957659CC

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
import org.apache.struts.action.DynaActionForm;

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
	public ActionForward spyExecute(ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,HttpServletResponse response)
		throws Exception {

		DynaActionForm ssf=(DynaActionForm)form;

		PhotoSessionData sessionData=getSessionData(request);
		String dims=(String)ssf.get("dims");
		sessionData.setOptimalDimensions(new PhotoDimensionsImpl(dims));

		getLogger().debug("Set viewing size to " + dims);

		return(mapping.findForward("next"));
	}

}

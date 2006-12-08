// Copyright (c) 2006  Dustin Sallings <dustin@spy.net>
// arch-tag: D4A9770A-876C-4363-9029-CB001A8A4FF6

package net.spy.photo.struts;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import net.spy.photo.PlaceFactory;

/**
 * List all of the places into <code>places</code>.
 */
public class ListPlacesAction extends PhotoAction {

	@Override
	protected ActionForward spyExecute(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res) throws Exception {

		req.setAttribute("places", PlaceFactory.getInstance().getObjects());

		return mapping.findForward("next");
	}

}

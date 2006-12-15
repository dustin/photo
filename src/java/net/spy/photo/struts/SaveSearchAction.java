// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>

package net.spy.photo.struts;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.spy.photo.PhotoSessionData;
import net.spy.photo.search.Search;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.DynaActionForm;

/**
 * Save a search.
 */
public class SaveSearchAction extends PhotoAction {

	/**
	 * Get an instance of SaveSearchAction.
	 */
	public SaveSearchAction() {
		super();
	}

	/**
	 * Process the save search.
	 */
	@Override
	public ActionForward spyExecute(ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,HttpServletResponse response)
		throws Exception {

		// Cast the form
		DynaActionForm ssf=(DynaActionForm)form;

		// Get the session data.
		PhotoSessionData sessionData=getSessionData(request);

		// Get the PhotoSearch object which manages the search save.
		Search search=Search.getInstance();
		String name=(String)ssf.get("name");
		search.saveSearch(name, (String)ssf.get("search"),
			sessionData.getUser());

		addMessage(request, MessageType.success,
				"Successfully saved search <q>" + name + "</q>");

		// If we made it this far, we were successful.
		return(mapping.findForward("next"));
	}

}

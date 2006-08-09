// Copyright (c) 2006  Dustin Sallings <dustin@spy.net>
// arch-tag: 1C7689C6-087C-49FB-AFE8-03CEB155EFE9

package net.spy.photo.struts;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.DynaActionForm;

import net.spy.photo.search.Search;

/**
 * Action to delete saved searches by ID.
 */
public class DeleteSearchAction extends PhotoAction {

	@Override
	protected ActionForward spyExecute(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res) throws Exception {
		DynaActionForm df=(DynaActionForm)form;
		Search.getInstance().deleteSearch((Integer)df.get("searchId"),
				getUser(req));
		ServletOutputStream os=res.getOutputStream();
		res.setContentType("text/plain");
		os.println("OK");
		return null;
	}

}

// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: SearchAction.java,v 1.1 2002/05/11 09:24:34 dustin Exp $

package net.spy.photo.struts;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.apache.struts.action.*;

import net.spy.photo.*;

/**
 * Perform a search.
 */
public class SearchAction extends Action {

	/**
	 * Get an instance of SearchAction.
	 */
	public SearchAction() {
		super();
	}

	/**
	 * Perform the action.
	 */
	public ActionForward perform(ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,HttpServletResponse response)
		throws IOException, ServletException {

		ActionForward rv=mapping.findForward("success");

		return(rv);
	}

}

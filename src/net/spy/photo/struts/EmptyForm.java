// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: EmptyForm.java,v 1.1 2002/09/16 01:51:03 dustin Exp $

package net.spy.photo.struts;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionForm;

/**
 * A form that doesn't do any processing.
 *
 * For whatever reason (probably documented), struts seems to be requiring
 * an ActionForm when processing a form that has parameters.
 */
public class EmptyForm extends ActionForm {

	/**
	 * Get an instance of EmptyForm.
	 */
	public EmptyForm() {
		super();
	}

}

// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>

package net.spy.photo.struts;


/**
 * A form that doesn't do any processing.
 *
 * For whatever reason (probably documented), struts seems to be requiring
 * an ActionForm when processing a form that has parameters.
 */
public class EmptyForm extends PhotoForm {

	/**
	 * Get an instance of EmptyForm.
	 */
	public EmptyForm() {
		super();
	}

}

// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: SaveSearchForm.java,v 1.2 2002/05/22 00:19:50 dustin Exp $

package net.spy.photo.struts;

import javax.servlet.http.HttpServletRequest;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

/**
 * Form used to save a search.
 */
public class SaveSearchForm extends ActionForm {

	private String name=null;
	private String search=null;

	/**
	 * Get an instance of SaveSearchForm.
	 */
	public SaveSearchForm() {
		super();
	}

	/**
	 * Set the name of the search.
	 */
	public void setName(String name) {
		this.name=name;
	}

	/**
	 * Get the name of the search.
	 */
	public String getName() {
		return(name);
	}

	/**
	 * Set the base64 encoded search.
	 */
	public void setSearch(String search) {
		this.search=search;
	}

	/**
	 * Get the base64 encoded search.
	 */
	public String getSearch() {
		return(search);
	}

	/**
	 * Validate the properties.
	 */
	public ActionErrors validate(ActionMapping mapping,
		HttpServletRequest request) {

		ActionErrors errors = new ActionErrors();

		if( (name==null) || (name.length() < 1)) {
			errors.add("name", new ActionError("error.savesearch.name"));
		}
		if( (search==null) || (search.length() < 1)) {
			errors.add("search", new ActionError("error.savesearch.search"));
		}

		return(errors);
	}

}

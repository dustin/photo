// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: SetSizeForm.java,v 1.1 2002/06/15 07:20:30 dustin Exp $

package net.spy.photo.struts;

import javax.servlet.http.HttpServletRequest;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import net.spy.photo.*;

/**
 * Form used for setting image size.
 */
public class SetSizeForm extends ActionForm {

	private String dims=null;
	private Exception dimException=null;

	/**
	 * Get an instance of SetSizeForm.
	 */
	public SetSizeForm() {
		super();
		dims="800x600";
	}

	/**
	 * Set the dimensions.
	 */
	public void setDims(String to) {
		try {
			PhotoDimensions dimsTmp=new PhotoDimensionsImpl(to);
			dims=to;
		} catch(IllegalArgumentException e) {
			e.printStackTrace();
			dimException=e;
		}
	}

	/**
	 * Get the dimensions from this form.
	 */
	public String getDims() {
		return(dims);
	}

	/**
	 * Validate the properties.
	 */
	public ActionErrors validate(ActionMapping mapping,
		HttpServletRequest request) {

		ActionErrors errors = new ActionErrors();
		if(dimException != null) {
			errors.add("dims", new ActionError("error.setsize.dims.e",
			dimException.getMessage()));
		} else if(dims == null) {
			errors.add("dims", new ActionError("error.setsize.dims"));
		}

		return(errors);
	}

	/**
	 * Reset all properties to their default values.
	 */
	public void reset(ActionMapping mapping, HttpServletRequest request) {
		dims="800x600";
		dimException=null;
	}

}

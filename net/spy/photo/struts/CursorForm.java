// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: CursorForm.java,v 1.6 2002/07/10 03:38:09 dustin Exp $

package net.spy.photo.struts;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

/**
 * Form for processing login requests.
 */
public class CursorForm extends ActionForm {

	private String startOffset=null;
	private String whichCursor=null;

	/**
	 * Get an instance of LoginForm.
	 */
	public CursorForm() {
		super();
	}

	/**
	 * Set the start offset for the current page.
	 */
	public void setStartOffset(String startOffset) {
		this.startOffset=startOffset;
	}

	/**
	 * Get the start offset for the current page (null if not set).
	 */
	public String getStartOffset() {
		return(startOffset);
	}

	/**
	 * Set the name of the cursor that needs to be adjusted.
	 *
	 * @param whichCursor must be <code>results</code> or
	 * <code>comments</code>
	 */
	public void setWhichCursor(String whichCursor) {
		this.whichCursor=whichCursor;
	}

	/**
	 * Get the name of the cursor that needs to be adjusted.
	 */
	public String getWhichCursor() {
		return(whichCursor);
	}

	/**
	 * Validate the properties.
	 *
	 * @param mapping The mapping used to select this instance
	 * @param request The servlet request we are processing
	 */
	public ActionErrors validate(ActionMapping mapping,
		HttpServletRequest request) {

		// If startOffset isn't a valid number, null it.
		try {
			if(startOffset!=null) {
				Integer.parseInt(startOffset);
			}
		} catch(NumberFormatException nfe) {
			nfe.printStackTrace();
			startOffset=null;
		}

		if(whichCursor!=null) {
			if(whichCursor.equals("results")) {
				// OK
			} else if(whichCursor.equals("comments")) {
				// OK
			} else {
				// Not OK, reset it
				whichCursor=null;
			}
		}

		// There will be no errors
		return(null);
	}

	/**
	 * Reset all properties to their default values.
	 *
	 * @param mapping The mapping used to select this instance
	 * @param request The servlet request we are processing
	 */
	public void reset(ActionMapping mapping, HttpServletRequest request) {
		startOffset=null;
		whichCursor=null;
	}

}

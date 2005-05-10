// Copyright (c) 2004  Dustin Sallings <dustin@spy.net>
// arch-tag: 32CCE484-5D6E-11D9-9E80-000A957659CC

package net.spy.photo.struts;

import org.apache.struts.action.ActionForm;

import net.spy.log.Logger;
import net.spy.log.LoggerFactory;

/**
 * Form with logging.
 */
public abstract class PhotoForm extends ActionForm {

	private Logger logger=null;

	/**
	 * Get an instance of PhotoForm.
	 */
	public PhotoForm() {
		super();
	}

	/** 
	 * Get a logger for this form.
	 */
	protected Logger getLogger() {
		if(logger==null) {
			logger=LoggerFactory.getLogger(getClass());
		}
		return(logger);
	}

}

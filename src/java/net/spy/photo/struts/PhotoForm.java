// Copyright (c) 2004  Dustin Sallings <dustin@spy.net>

package net.spy.photo.struts;

import net.spy.log.Logger;
import net.spy.log.LoggerFactory;

import org.apache.struts.action.ActionForm;

/**
 * Form with logging.
 */
public abstract class PhotoForm extends ActionForm {

	private Logger logger=null;

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

/*
 * Copyright (c) 1999 Dustin Sallings
 *
 * $Id: PhotoHelper.java,v 1.13 2003/05/27 03:36:22 dustin Exp $
 */

package net.spy.photo;

import net.spy.SpyObject;

/**
 * Superclass for general supplemental classes.
 */
public class PhotoHelper extends SpyObject { 

	private PhotoConfig conf=null;

	/**
	 * Instantiate the helper class.
	 */
	public PhotoHelper() {
		super();
		conf = PhotoConfig.getInstance();
	}

	/**
	 * Get the configuration instance contained in this helper.
	 */
	protected PhotoConfig getConfig() {
		return(conf);
	}

}

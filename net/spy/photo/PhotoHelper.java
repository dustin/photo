/*
 * Copyright (c) 1999 Dustin Sallings
 *
 * $Id: PhotoHelper.java,v 1.12 2002/07/10 03:38:08 dustin Exp $
 */

package net.spy.photo;

/**
 * Superclass for general supplemental classes.
 */
public class PhotoHelper extends Object { 

	private PhotoConfig conf=null;

	/**
	 * Instantiate the helper class.
	 */
	public PhotoHelper() {
		super();
		conf = new PhotoConfig();
	}

	/**
	 * Get the configuration instance contained in this helper.
	 */
	protected PhotoConfig getConfig() {
		return(conf);
	}

	/**
	 * Log a message.
	 */
	protected void log(String message) {
		System.err.println(getClass().getName() + ": " + message);
	}
}


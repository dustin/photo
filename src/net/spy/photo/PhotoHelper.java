// Copyright (c) 1999 Dustin Sallings
// arch-tag: 0ADFC353-5D6D-11D9-BEF0-000A957659CC

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

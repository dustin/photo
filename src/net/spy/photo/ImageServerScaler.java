// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: ImageServerScaler.java,v 1.2 2002/07/10 03:38:08 dustin Exp $

package net.spy.photo;

import net.spy.SpyConfig;

/**
 * Interface for scaling images.
 */
public abstract class ImageServerScaler extends Object {

	private boolean debug=false;
	private SpyConfig conf=null;

	/**
	 * Scale the image.
	 */
	public abstract PhotoImage scaleImage(PhotoImage in, PhotoDimensions dim)
		throws Exception;

	/**
	 * Set the configuration for this instance.
	 */
	public void setConfig(SpyConfig conf) {
		this.conf=conf;
	}

	/**
	 * Get the config.
	 *
	 * @return the SpyConfig for this instance
	 */
	protected SpyConfig getConf() {
		return(conf);
	}

	/**
	 * Log the given message.
	 */
	protected void log(String msg) {
		System.err.println(msg);
	}

	/**
	 * If debug is enabled, log the given message.
	 */
	protected void debug(String msg) {
		if(debug) {
			log(msg);
		}
	}

	/**
	 * Set the debug value.
	 */
	protected void setDebug(boolean to) {
		debug=to;
	}

}
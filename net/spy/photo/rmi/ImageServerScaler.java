// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: ImageServerScaler.java,v 1.1 2002/06/16 07:14:13 dustin Exp $

package net.spy.photo.rmi;

import net.spy.*;

import net.spy.photo.*;

/**
 * Interface for scaling images.
 */
public abstract class ImageServerScaler extends Object {

	private boolean debug=false;
	protected SpyConfig conf=null;

	/**
	 * Scale the image.
	 */
	public abstract PhotoImage scaleImage(PhotoImage in, PhotoDimensions dim)
		throws Exception;

	/**
	 * Set the configuration for this instance.
	 */
	void setConfig(SpyConfig conf) {
		this.conf=conf;
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

// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: ImageServerScaler.java,v 1.2 2002/07/10 03:38:08 dustin Exp $

package net.spy.photo;

import net.spy.SpyConfig;
import net.spy.SpyObject;

/**
 * Interface for scaling images.
 */
public abstract class ImageServerScaler extends SpyObject {

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

}

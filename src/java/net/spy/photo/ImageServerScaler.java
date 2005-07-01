// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
// arch-tag: DEDCD8C7-5D6C-11D9-BDB9-000A957659CC

package net.spy.photo;

import net.spy.SpyObject;
import net.spy.util.SpyConfig;

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
	public void setConfig(SpyConfig to) {
		this.conf=to;
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
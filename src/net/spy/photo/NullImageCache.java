// Copyright (c) 2004  Dustin Sallings <dustin@spy.net>

package net.spy.photo;

import net.spy.SpyObject;

/**
 * ImageCache implementation that doesn't perform any caching.
 */
public class NullImageCache extends SpyObject implements ImageCache {

	/**
	 * Get an instance of NullImageCache.
	 */
	public NullImageCache() {
		super();
	}

	public PhotoImage getImage(String key) throws PhotoException {
		if(getLogger().isDebugEnabled()) {
			getLogger().debug("Getting image for key:  " + key);
		}
		return(null);
	}

	public void putImage(String key, PhotoImage image) throws PhotoException {
		if(getLogger().isDebugEnabled()) {
			getLogger().debug("Requested to store image for key:  " + key);
		}
	}

}

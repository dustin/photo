// Copyright (c) 2004  Dustin Sallings <dustin@spy.net>
// arch-tag: EF659312-5D6C-11D9-8CBF-000A957659CC

package net.spy.photo.impl;

import net.spy.SpyObject;

import net.spy.photo.ImageCache;
import net.spy.photo.PhotoImage;
import net.spy.photo.PhotoException;

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

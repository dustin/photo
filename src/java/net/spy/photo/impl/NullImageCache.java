// Copyright (c) 2004  Dustin Sallings <dustin@spy.net>

package net.spy.photo.impl;

import net.spy.SpyObject;
import net.spy.photo.ImageCache;
import net.spy.photo.PhotoException;

/**
 * ImageCache implementation that doesn't perform any caching.
 */
public class NullImageCache extends SpyObject implements ImageCache {

	public byte[] getImage(String key) throws PhotoException {
		if(getLogger().isDebugEnabled()) {
			getLogger().debug("Getting image for key:  " + key);
		}
		return(null);
	}

	public void putImage(String key, byte[] image) throws PhotoException {
		if(getLogger().isDebugEnabled()) {
			getLogger().debug("Requested to store image for key:  " + key);
		}
	}

	public boolean willStore(String key, byte[] image) {
		return false;
	}

}

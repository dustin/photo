// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: LocalImageCacheImpl.java,v 1.5 2002/09/13 20:28:46 dustin Exp $

package net.spy.photo;

import java.io.IOException;

import net.spy.cache.DiskCache;

/**
 * ImageCache that uses local files.
 */
public class LocalImageCacheImpl extends Object implements ImageCache {

	private DiskCache cache=null;

	/**
	 * Get an instance of LocalImageCacheImpl.
	 */
	public LocalImageCacheImpl() {
		super();

		PhotoConfig conf=PhotoConfig.getInstance();
		cache=new DiskCache(conf.get("cache.dir", "/var/tmp/photocache"));
	}

	/**
	 * @see ImageCache
	 */
	public PhotoImage getImage(String key) throws PhotoException {
		PhotoImage rv=(PhotoImage)cache.get(key);
		return(rv);
	}

	/**
	 * @see ImageCache
	 */
	public void putImage(String key, PhotoImage image) throws PhotoException {
		cache.put(key, image);
	}

}


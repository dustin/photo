// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>

package net.spy.photo.impl;

import net.spy.photo.ImageCache;
import net.spy.photo.PhotoConfig;
import net.spy.photo.PhotoException;
import net.spy.photo.PhotoImage;
import net.spy.photo.rmi.RHash;

/**
 * ImageCache that uses RMI.
 */
public class RemoteImageCacheImpl extends Object implements ImageCache {

	private RHash rhash=null;

	/**
	 * Get an instance of RemoteImageCacheImpl.
	 */
	public RemoteImageCacheImpl() throws Exception {
		super();

		PhotoConfig conf=PhotoConfig.getInstance();
		rhash=new RHash(conf.get("rhash.url", "//localhost/RObjectServer"));
	}

	/**
	 * @see ImageCache
	 */
	public PhotoImage getImage(String key) throws PhotoException {
		return( (PhotoImage)rhash.get(key) );
	}

	/**
	 * @see ImageCache
	 */
	public void putImage(String key, PhotoImage image) throws PhotoException {
		rhash.put(key, image);
	}

}


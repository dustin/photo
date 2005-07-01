// Copyright (c) 2004  Dustin Sallings <dustin@spy.net>
// arch-tag: 5350C973-5D6D-11D9-9665-000A957659CC

package net.spy.photo.impl;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;

import net.spy.SpyObject;
import net.spy.photo.ImageCache;
import net.spy.photo.PhotoException;
import net.spy.photo.PhotoImage;

/**
 * ImageCache implementation that keeps soft references to images in memory.
 */
public class SoftImageCache extends SpyObject implements ImageCache {

	private Map<String, Reference<PhotoImage>> store=null;

	/**
	 * Get an instance of SoftImageCache.
	 */
	public SoftImageCache() {
		super();
		store=new HashMap<String, Reference<PhotoImage>>();
	}

	public PhotoImage getImage(String key) throws PhotoException {
		PhotoImage rv=null;
		Reference<PhotoImage> r=store.get(key);
		if(r != null) {
			rv=r.get();
		}
		return(rv);
	}

	public void putImage(String key, PhotoImage image) throws PhotoException {
		getLogger().info("Caching " + key);
		store.put(key, new SoftReference<PhotoImage>(image));
	}

}

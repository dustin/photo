// Copyright (c) 2004  Dustin Sallings <dustin@spy.net>
// arch-tag: 5350C973-5D6D-11D9-9665-000A957659CC

package net.spy.photo;

import java.util.Map;
import java.util.HashMap;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;

import net.spy.SpyObject;

/**
 * ImageCache implementation that keeps soft references to images in memory.
 */
public class SoftImageCache extends SpyObject implements ImageCache {

	private Map store=null;

	/**
	 * Get an instance of SoftImageCache.
	 */
	public SoftImageCache() {
		super();
		store=new HashMap();
	}

	public PhotoImage getImage(String key) throws PhotoException {
		PhotoImage rv=null;
		Reference r=(Reference)store.get(key);
		if(r != null) {
			rv=(PhotoImage)r.get();
		}
		return(rv);
	}

	public void putImage(String key, PhotoImage image) throws PhotoException {
		getLogger().info("Caching " + key);
		store.put(key, new SoftReference(image));
	}

}

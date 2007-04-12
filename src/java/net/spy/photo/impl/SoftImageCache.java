// Copyright (c) 2004  Dustin Sallings <dustin@spy.net>

package net.spy.photo.impl;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;

import net.spy.SpyObject;
import net.spy.photo.ImageCache;
import net.spy.photo.PhotoException;
import net.spy.photo.ShutdownHook;

/**
 * ImageCache implementation that keeps soft references to images in memory.
 */
public class SoftImageCache extends SpyObject
	implements ImageCache, ShutdownHook {

	private Map<String, Reference<byte[]>> store=
		new HashMap<String, Reference<byte[]>>();

	public byte[] getImage(String key) throws PhotoException {
		byte[] rv=null;
		Reference<byte[]> r=store.get(key);
		if(r != null) {
			rv=r.get();
		}
		return(rv);
	}

	public void putImage(String key, byte[] image) throws PhotoException {
		getLogger().info("Caching " + key);
		store.put(key, new SoftReference<byte[]>(image));
	}

	public void onShutdown() throws Exception {
		store.clear();
		store=null;
	}

	public boolean willStore(String key, byte[] image) {
		return true;
	}

}

// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>

package net.spy.photo.impl;

import net.spy.cache.DiskCache;
import net.spy.photo.ImageCache;
import net.spy.photo.PhotoConfig;
import net.spy.photo.PhotoException;
import net.spy.stat.ComputingStat;
import net.spy.stat.Stats;

/**
 * ImageCache that uses local files.
 */
public class LocalImageCacheImpl extends Object implements ImageCache {

	private DiskCache cache=null;

	private ComputingStat hitStat=null;
	private ComputingStat missStat=null;

	/**
	 * Get an instance of LocalImageCacheImpl.
	 */
	public LocalImageCacheImpl() {
		super();

		PhotoConfig conf=PhotoConfig.getInstance();
		cache=new DiskCache(conf.get("cache.dir", "/var/tmp/photocache"),
			conf.getInt("cache.dcache.lrusize", 100));

		hitStat=Stats.getComputingStat("localcache.hit");
		missStat=Stats.getComputingStat("localcache.miss");
	}

	/**
	 * @see ImageCache
	 */
	public byte[] getImage(String key) throws PhotoException {
		long start=System.currentTimeMillis();
		byte[] rv=(byte[])cache.get(key);
		long end=System.currentTimeMillis();
		// Add our stats.
		(rv==null?missStat:hitStat).add(end-start);
		return(rv);
	}

	/**
	 * @see ImageCache
	 */
	public void putImage(String key, byte[] image) throws PhotoException {
		cache.put(key, image);
	}

	public boolean willStore(String key, byte[] image) {
		return true;
	}

}


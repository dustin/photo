/*
 * Copyright (c) 1999 Dustin Sallings
 *
 * $Id: PhotoCache.java,v 1.3 2000/10/13 06:57:56 dustin Exp $
 */

package net.spy.photo;

import java.util.*;

public class PhotoCache extends Object {
	protected static Hashtable cacheStore=null;
	protected static PhotoCacheCleaner cacheCleaner=null;

	public PhotoCache() {
		super();

		init();
	}

	public void store(String key, Object value, long cache_time) {
		PhotoCacheItem i=new PhotoCacheItem(key, value, cache_time);
		synchronized(cacheStore) {
			cacheStore.put(key, i);
		}
	}

	public Object get(String key) {
		Object ret=null;
		synchronized(cacheStore) {
			PhotoCacheItem i=(PhotoCacheItem)cacheStore.get(key);
			if(i!=null && (!i.expired())) {
				ret=i.getObject();
			}
		}
		return(ret);
	}

	public void uncache(String key) {
		synchronized(cacheStore) {
			cacheStore.remove(key);
		}
	}

	public void uncacheLike(String keystart) {
		synchronized(cacheStore) {
			for(Enumeration e=cacheStore.keys(); e.hasMoreElements(); ) {
				String key=(String)e.nextElement();

				// If this matches, kill it.
				if(key.startsWith(keystart)) {
					cacheStore.remove(key);
				}
			} // for loop
		} // lock
	}

	protected synchronized void init() {
		if(cacheStore==null) {
			cacheStore=new Hashtable();
		}

		// start or restart the cache cleaner if needed.
		if(cacheCleaner==null || (!cacheCleaner.isAlive())) {
			cacheCleaner=new PhotoCacheCleaner(cacheStore);
			cacheCleaner.setDaemon(true);
			cacheCleaner.start();
		}
	}
}

/*
 * Copyright (c) 1999 Dustin Sallings
 *
 * $Id: PhotoCacheCleaner.java,v 1.1 2000/07/05 01:03:41 dustin Exp $
 */

package net.spy.photo;

import java.util.*;

public class PhotoCacheCleaner extends Thread {
	protected Hashtable cacheStore=null;

	public PhotoCacheCleaner(Hashtable cacheStore) {
		super();
		this.cacheStore=cacheStore;
	}

	protected void cleanup() throws Exception {
		long now=System.currentTimeMillis();
		synchronized(cacheStore) {
			for(Enumeration e=cacheStore.elements(); e.hasMoreElements(); ) {
				PhotoCacheItem i=(PhotoCacheItem)e.nextElement();
				if(i.expired()) {
					cacheStore.remove(i.getKey());
				}
			}
		}
	}

	public void run() {
		// Just loop through periodically and make sure expired cache
		// entries are removed.
		while(true) {
			try {
				sleep(300*1000);
				cleanup();
			} catch(Exception e) {
				// Just try again.
			}
		}
	}
}

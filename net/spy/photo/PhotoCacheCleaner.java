/*
 * Copyright (c) 1999 Dustin Sallings
 *
 * $Id: PhotoCacheCleaner.java,v 1.2 2000/10/13 06:57:56 dustin Exp $
 */

package net.spy.photo;

import java.util.*;

public class PhotoCacheCleaner extends Thread {
	protected Hashtable cacheStore=null;

	// How many cleaning passes we've done.
	protected int passes=0;

	public PhotoCacheCleaner(Hashtable cacheStore) {
		super();
		this.cacheStore=cacheStore;
		this.setName("PhotoCacheCleaner");
	}

	public String toString() {
		return(super.toString() + " - " + passes + " runs");
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
		passes++;
	}

	public void run() {
		// Give it ten passes
		while(passes<12) {
			try {
				sleep(300*1000);
				cleanup();
			} catch(Exception e) {
				// Just try again.
			}
		}
	}
}

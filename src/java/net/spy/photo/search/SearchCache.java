// arch-tag: 7E8AD7F0-8462-4D14-870F-6A953220232F

package net.spy.photo.search;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import net.spy.SpyThread;

/**
 * Cache for searches.
 */
public class SearchCache extends SpyThread implements Observer {

	private static SearchCache instance=null;

	private int hits=0;
	private int misses=0;
	private int dequeued=0;
	private int stores=0;
	private int maxsize=0;

	private Map<Object, SoftReference> cache=null;
	private Map<SoftReference, Object> keyMap=null;
	private ReferenceQueue<SearchResults> refQueue=null;
	private boolean running=true;

	private SearchCache() {
		super("SearchCache");
	}

	public static synchronized SearchCache getInstance() {
		if(instance == null) {
			instance=new SearchCache();
			instance.cache=new HashMap();
			instance.keyMap=new HashMap();
			instance.refQueue=new ReferenceQueue<SearchResults>();
			instance.start();

			// Register to receive updates when the search index updates
			SearchIndex.getInstance().addObserver(instance);
		}
		return(instance);
	}

	/**
	 * Clear the cache.
	 */
	public synchronized void clear() {
		cache.clear();
		keyMap.clear();
	}

	/** 
	 * Receive updates indicating the search indexes have been updated.
	 */
	public void update(Observable o, Object arg) {
		clear();
	}

	/**
	 * Get an entry from the search cache.
	 * 
	 * @param f the search form
	 * @param u the user
	 * @param d the requested dimensions
	 */
	public synchronized Object get(Object key) {
		SoftReference<SearchResults> sr=cache.get(key);
		Object rv=null;
		if(sr != null) {
			rv=sr.get();
			if(rv != null) {
				hits++;
			}
		} else {
			misses++;
		}
		return(rv);
	}

	/**
	 * Store an object in the search cache.
	 * 
	 * @param key the cache key
	 * @param o the value to cache
	 */
	public synchronized void store(Object key, Object o) {
		SoftReference ref=new SoftReference(o, refQueue);
		cache.put(key, ref);
		keyMap.put(ref, key);
		stores++;
		if(cache.size() > maxsize) {
			maxsize++;
		}
	}

	/**
	 * Shut down this cache.
	 */
	public synchronized void shutdown() {
		cache.clear();
		running=false;
		interrupt();
	}

	public void run() {
		while(running) {
			try {
				Reference rce=refQueue.remove();
				dequeued++;
				synchronized(this) {
					cache.remove(keyMap.remove(rce));
				}
			} catch(InterruptedException e) {
				getLogger().info("Interrupted");
			}
		}
	}

	public synchronized String toString() {
		return(super.toString() + " - stores: " + stores + ", hits: " + hits
				+ ", misses: " + misses + ", dequeued: " + dequeued
				+ ", maxsize: " + maxsize
				+ ", current: " + cache.size() + "/" + keyMap.size());
	}

}

// arch-tag: 7E8AD7F0-8462-4D14-870F-6A953220232F

package net.spy.photo.search;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.lang.ref.ReferenceQueue;
import java.util.HashMap;
import java.util.Map;
import java.util.Observer;
import java.util.Observable;

import net.spy.SpyThread;
import net.spy.photo.PhotoDimensions;
import net.spy.photo.User;
import net.spy.photo.struts.SearchForm;

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

	private Map<CacheKey, SoftReference<SearchResults>> cache=null;
	private Map<SoftReference<SearchResults>, CacheKey> keyMap=null;
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
	public synchronized SearchResults get(SearchForm f, User u,
		PhotoDimensions d) {
		SoftReference<SearchResults> sr=cache.get(new CacheKey(u, d, f));
		SearchResults rv=null;
		if(sr != null) {
			rv=sr.get();
			if(rv != null) {
				hits++;
				rv=(SearchResults)rv.clone();
			}
		} else {
			misses++;
		}
		return(rv);
	}

	/**
	 * Store an entry in the search cache.
	 * 
	 * @param f the search form
	 * @param u the user
	 * @param d the dimensions
	 * @param r the results to store
	 */
	public synchronized void store(SearchForm f, User u, PhotoDimensions d,
			SearchResults r) {
		CacheKey ck=new CacheKey(u, d, f);
		SoftReference ref=new SoftReference(r.clone(), refQueue);
		cache.put(ck, ref);
		keyMap.put(ref, ck);
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

	private static class CacheKey {
		private int uid=0;
		private String dims=null;
		private SearchForm form=null;

		public CacheKey(User u, PhotoDimensions d, SearchForm f) {
			super();
			uid=u.getId();
			dims=d.getWidth() + "x" + d.getHeight();
			form=f;
		}

		public boolean equals(Object o) {
			CacheKey ck=(CacheKey)o;
			return(uid == ck.uid && dims.equals(ck.dims)
				&& form.equals(ck.form));
		}

		public int hashCode() {
			return(uid ^ dims.hashCode() ^ form.hashCode());
		}
		
	}

}

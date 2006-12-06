// arch-tag: 7E8AD7F0-8462-4D14-870F-6A953220232F

package net.spy.photo.search;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import net.spy.SpyThread;

/**
 * Cache for searches.
 */
public class SearchCache extends SpyThread {

	private static SearchCache instance=null;

	private AtomicInteger hits=new AtomicInteger(0);
	private AtomicInteger misses=new AtomicInteger(0);
	private AtomicInteger dequeued=new AtomicInteger(0);
	private AtomicInteger stores=new AtomicInteger(0);
	private AtomicInteger maxsize=new AtomicInteger(0);

	private ConcurrentMap<Object, SoftReference<SearchResults>> cache=null;
	private ConcurrentMap<SoftReference<?>, Object> keyMap=null;
	private ReferenceQueue<SearchResults> refQueue=null;
	private boolean running=true;

	private SearchCache() {
		super("SearchCache");
	}

	public static void setup() {
		assert instance == null : "Already running";
		instance=new SearchCache();
		instance.cache=
			new ConcurrentHashMap<Object, SoftReference<SearchResults>>();
		instance.keyMap=new ConcurrentHashMap<SoftReference<?>, Object>();
		instance.refQueue=new ReferenceQueue<SearchResults>();
		instance.start();
	}

	public static SearchCache getInstance() {
		assert instance != null : "SearchCache not set up.";
		return(instance);
	}

	/**
	 * Clear the cache.
	 */
	public void clear() {
		keyMap.clear();
		cache.clear();
	}

	/**
	 * Get an entry from the search cache.
	 * 
	 * @param f the search form
	 * @param u the user
	 * @param d the requested dimensions
	 */
	public Object get(Object key) {
		SoftReference<SearchResults> sr=cache.get(key);
		Object rv=null;
		if(sr != null) {
			rv=sr.get();
		}
		(rv == null ? misses : hits).incrementAndGet();
		return(rv);
	}

	/**
	 * Store an object in the search cache.
	 * 
	 * @param key the cache key
	 * @param o the value to cache
	 */
	// This is a little scary.  Not sure what kinds of things are added here.
	@SuppressWarnings("unchecked")
	public void store(Object key, Object o) {
		SoftReference ref=new SoftReference(o, refQueue);
		keyMap.put(ref, key);
		cache.put(key, ref);
		stores.incrementAndGet();
		int prevSize=maxsize.get();
		int cacheSize=cache.size();
		if(cacheSize > prevSize) {
			maxsize.compareAndSet(prevSize, cacheSize);
		}
	}

	/**
	 * Shut down this cache.
	 */
	public void shutdown() {
		running=false;
		interrupt();
		cache.clear();
	}

	@Override
	public void run() {
		while(running) {
			try {
				Reference<?> rce=refQueue.remove();
				dequeued.incrementAndGet();
				cache.remove(keyMap.remove(rce));
			} catch(InterruptedException e) {
				getLogger().info("Interrupted");
			}
		}
	}

	@Override
	public String toString() {
		return(super.toString() + " - stores: " + stores + ", hits: " + hits
				+ ", misses: " + misses + ", dequeued: " + dequeued
				+ ", maxsize: " + maxsize
				+ ", current: " + cache.size() + "/" + keyMap.size());
	}

}

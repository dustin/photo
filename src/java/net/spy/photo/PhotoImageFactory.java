// Copyright (c) 2004  Dustin Sallings <dustin@spy.net>

package net.spy.photo;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicReference;

import net.spy.db.Savable;
import net.spy.db.Saver;
import net.spy.db.savables.CollectionSavable;
import net.spy.factory.CacheRefresher;
import net.spy.factory.GenFactory;
import net.spy.photo.impl.DBImageSource;
import net.spy.photo.search.SearchIndex;

/**
 * ImageData factory.
 */
public class PhotoImageFactory extends GenFactory<PhotoImage> {

	private static final String CACHE_KEY="image_data";
	private static final long CACHE_TIME=86400000;

	/**
	 * Default delay for deferred recache.  This is a bit of a long time as it
	 * serves only as a safety net should a but be discovered in the hot cache
	 * refresh code.
	 */
	public static final long RECACHE_DELAY=300000;

	private static AtomicReference<PhotoImageFactory> instanceRef=
		new AtomicReference<PhotoImageFactory>(null);

	private PhotoImageSource source=null;

	/**
	 * Get an instance of PhotoImageFactory.
	 */
	private PhotoImageFactory() {
		super(CACHE_KEY, CACHE_TIME);
		source=new DBImageSource();
	}

	/** 
	 * Get an instance of the PhotoImageFactory.
	 */
	public static PhotoImageFactory getInstance() {
		PhotoImageFactory rv=instanceRef.get();
		if(rv == null) {
			synchronized(PhotoImageFactory.class) {
				rv=new PhotoImageFactory();
				if(! instanceRef.compareAndSet(null, rv)) {
					rv=instanceRef.get();
					assert rv != null;
				}
			}
		}
		return rv;
	}

	/** 
	 * Get the instances of PhotoImage.
	 */
	@Override
	protected Collection<PhotoImage> getInstances() {
		// Get the source images
		getLogger().info("Fetching images.");
		Collection<PhotoImage> images=source.getImages();
		getLogger().info("Loaded " + images.size() + " images from "
			+ source.getClass().getName());
		return(images);
	}

	/** 
	 * Store a savable and optionally recache the instances.
	 */
	public void store(Savable ob, boolean recache, long recacheDelay)
		throws Exception {
		Saver s=new Saver(PhotoConfig.getInstance());
		s.save(ob);
		long start=System.currentTimeMillis();
		if(ob instanceof PhotoImage) {
			cacheInstance((PhotoImage)ob);
		} else if(ob instanceof CollectionSavable) {
			// XXX: Horrible abstraction leak.
			CollectionSavable cs=(CollectionSavable)ob;
			for(Savable pid : cs.getPostSavables(null)) {
				cacheInstance((PhotoImage)pid);
			}
		} else {
			assert false : "Unexpected savable type: " + ob.getClass();
		}
		SearchIndex.update(getObjects());
		getLogger().info("Updated in place and recached in %dms",
				System.currentTimeMillis() - start);
		if(recache) {
			CacheRefresher.getInstance().recache(this,
					System.currentTimeMillis(), recacheDelay);
		}
	}

	@Override
	public void recache() {
		// When recaching, also update the search index.
		long start1=System.currentTimeMillis();
		super.recache();
		long start2=System.currentTimeMillis();
		SearchIndex.update(getObjects());
		long end=System.currentTimeMillis();
		getLogger().info(
				"Recache completed in %dms plus %dms more for indexing",
				start2-start1, end-start2);
	}

	/** 
	 * Store a savable and recache the instances.
	 */
	public void store(Savable ob, boolean recache) throws Exception {
		store(ob, true, RECACHE_DELAY);
	}

	/** 
	 * Store a savable and recache the instances.
	 */
	public void store(Savable ob) throws Exception {
		store(ob, true);
	}

	public static class NoSuchPhotoException extends RuntimeException {
		public NoSuchPhotoException(int id) {
			super("No such photo:  " + id);
		}
	}

}

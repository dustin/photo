// Copyright (c) 2004  Dustin Sallings <dustin@spy.net>
// arch-tag: 10856BB1-5D6D-11D9-A5A4-000A957659CC

package net.spy.photo;

import java.util.Collection;

import net.spy.db.Savable;
import net.spy.db.Saver;
import net.spy.db.savables.CollectionSavable;
import net.spy.factory.CacheRefresher;
import net.spy.factory.GenFactory;
import net.spy.photo.impl.DBImageDataSource;
import net.spy.photo.search.SearchIndex;

/**
 * ImageData factory.
 */
public class PhotoImageDataFactory extends GenFactory<PhotoImageData> {

	private static final String CACHE_KEY="image_data";
	private static final long CACHE_TIME=86400000;

	/**
	 * Default delay for deferred recache.  This is a bit of a long time as it
	 * serves only as a safety net should a but be discovered in the hot cache
	 * refresh code.
	 */
	public static final long RECACHE_DELAY=300000;

	private static PhotoImageDataFactory instance=null;

	private PhotoImageDataSource source=null;

	/**
	 * Get an instance of PhotoImageDataFactory.
	 */
	private PhotoImageDataFactory() {
		super(CACHE_KEY, CACHE_TIME);
		source=new DBImageDataSource();
	}

	/** 
	 * Get an instance of the PhotoImageDataFactory.
	 */
	public static synchronized PhotoImageDataFactory getInstance() {
		if(instance == null) {
			instance=new PhotoImageDataFactory();
		}
		return(instance);
	}

	/** 
	 * Get the instances of PhotoImageData.
	 */
	protected Collection<PhotoImageData> getInstances() {
		// Get the source images
		getLogger().info("Fetching images.");
		Collection<PhotoImageData> images=source.getImages();
		getLogger().info("Loaded " + images.size() + " images from "
			+ source.getClass().getName());
		// Update the index cache
		SearchIndex si=SearchIndex.getInstance();
		si.update(images);
		return(images);
	}

	/** 
	 * Handle a missing image.
	 */
	protected PhotoImageData handleNullLookup(int id) {
		throw new NoSuchPhotoException(id);
	}

	/** 
	 * Store a savable and optionally recache the instances.
	 */
	public void store(Savable ob, boolean recache, long recacheDelay)
		throws Exception {
		Saver s=new Saver(PhotoConfig.getInstance());
		s.save(ob);
		long start=System.currentTimeMillis();
		if(ob instanceof PhotoImageData) {
			getCache().cacheInstance((PhotoImageData)ob);
			SearchIndex.getInstance().update(getObjects());
		} else if(ob instanceof CollectionSavable) {
			// XXX: Horrible abstraction leak.
			CollectionSavable cs=(CollectionSavable)ob;
			for(Savable pid : cs.getPostSavables(null)) {
				getCache().cacheInstance((PhotoImageData)pid);
			}
			SearchIndex.getInstance().update(getObjects());
		} else {
			assert false : "Unexpected savable type: " + ob.getClass();
		}
		getLogger().info("Updated in place and recached in %dms",
				System.currentTimeMillis() - start);
		if(recache) {
			CacheRefresher.getInstance().recache(this,
					System.currentTimeMillis(), recacheDelay);
		}
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

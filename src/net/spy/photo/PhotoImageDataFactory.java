// Copyright (c) 2004  Dustin Sallings <dustin@spy.net>

package net.spy.photo;

import java.util.Collection;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;

import net.spy.SpyObject;
import net.spy.cache.SpyCache;

/**
 * ImageData factory.
 */
public class PhotoImageDataFactory extends SpyObject {

	private static final String CACHE_KEY="image_data";
	private static final long CACHE_TIME=3600000;

	private static PhotoImageDataFactory instance=null;

	private PhotoImageDataSource source=null;

	/**
	 * Get an instance of PhotoImageDataFactory.
	 */
	private PhotoImageDataFactory() {
		super();
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

	private Map getCache() throws Exception {
		SpyCache sc=SpyCache.getInstance();
		Map rv=(Map)sc.get(CACHE_KEY);
		if(rv == null) {
			// Get the source images
			getLogger().info("Fetching images.");
			Collection images=source.getImages();
			getLogger().info("Loaded " + images.size() + " images from "
				+ source.getClass().getName());
			// Map all of the images
			rv=new HashMap();
			for(Iterator i=images.iterator(); i.hasNext(); ) {
				PhotoImageData pid=(PhotoImageData)i.next();
				rv.put(new Integer(pid.getId()), pid);
			}
			// Update the index cache
			SearchIndex si=SearchIndex.getInstance();
			si.update(images);
			// Cache it
			sc.store(CACHE_KEY, rv, CACHE_TIME);
		}
		return(rv);
	}

	/** 
	 * Clear the image data cache.
	 */
	public void clearCache() {
		SpyCache sc=SpyCache.getInstance();
		sc.uncache(CACHE_KEY);
	}

	/** 
	 * Get the PhotoImageData for the given ID.
	 * 
	 * @param id the given ID
	 * @return a PhotoImageData instance
	 * @throws Exception if we cannot load the data
	 */
	public PhotoImageData getData(int id) throws Exception {
		Map m=getCache();
		PhotoImageData rv=(PhotoImageData)m.get(new Integer(id));
		if(rv == null) {
			throw new Exception("No such image:  " + id);
		}
		return(rv);
	}

}

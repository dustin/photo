// Copyright (c) 1999 Dustin Sallings
// arch-tag: 196C8F31-5D6D-11D9-92C2-000A957659CC

package net.spy.photo;

import java.lang.ref.SoftReference;

import net.spy.SpyObject;
import net.spy.cache.SpyCache;
import net.spy.photo.impl.PhotoDimensionsImpl;

/**
 * Get images from the image server.
 */
public class PhotoImageHelper extends SpyObject { 

	private int imageId=-1;

	/**
	 * Get a PhotoHelper for the given image on behalf.
	 */
	public PhotoImageHelper(int which) throws Exception {
		super();
		this.imageId = which;
	}

	/**
	 * Get the full image on behalf of a user.
	 */
	public PhotoImage getImage(User user, PhotoDimensions dim)
		throws Exception {

		Persistent.getSecurity().checkAccess(user, imageId);
		return(getImage(dim));
	}

	/**
	 * Get a full-size image.
	 */
	public PhotoImage getImage() throws Exception {
		return(getImage(null));
	}

	/**
	 * Get the full image.
	 */
	public PhotoImage getImage(PhotoDimensions dim)
		throws Exception {

		PhotoImage rv=null;
		SpyCache cache=SpyCache.getInstance();

		StringBuffer keyb=new StringBuffer(64);
		keyb.append("img_");
		keyb.append(imageId);
		if(dim!=null) {
			keyb.append("_");
			keyb.append(dim.getWidth());
			keyb.append("x");
			keyb.append(dim.getHeight());
		}
		String key=keyb.toString();

		// Always check the cache first
		rv=(PhotoImage)cache.get(key);
		if(rv==null) {
			if(getLogger().isDebugEnabled()) {
				getLogger().debug("Cache miss on " + key);
				// Grab it from the server
				getLogger().debug("Grabbing " + key + " from image server");
			}

			ImageServer server=Persistent.getImageServer();
			rv=server.getImage(imageId, dim);

			// If it's small enough, cache it.
			if(rv.size() < 32768) {
				cache.store(key, new SoftReference<PhotoImage>(rv), 10*60*1000);
			}
		}

		return(rv);
	}

	/**
	 * Get the thumbnail for an image on behalf of a user.
	 */
	public PhotoImage getThumbnail(int uid) throws Exception {
		Persistent.getSecurity().checkAccess(uid, imageId);
		return(getThumbnail());
	}

	/**
	 * Get the thumbnail for an image.
	 */
	public PhotoImage getThumbnail() throws Exception {
		PhotoConfig cf=PhotoConfig.getInstance();
		PhotoDimensions pdim= new PhotoDimensionsImpl(cf.get("thumbnail_size"));
		return(getImage(pdim));
	}

	/** 
	 * Get the size of this image as a thumbnail.
	 */
	public PhotoDimensions getThumbnailSize() throws Exception {
		PhotoDimensions rv=null;
		SpyCache cache=SpyCache.getInstance();
		String key="ph_thumbnail_size_" + imageId;
		rv=(PhotoDimensions)cache.get(key);
		if(rv == null) {
			PhotoImage pi=getThumbnail();
			rv=new PhotoDimensionsImpl(pi.getWidth(), pi.getHeight());
			cache.store(key, new SoftReference<PhotoDimensions>(rv), 1800000);
		}

		return(rv);
	}
}

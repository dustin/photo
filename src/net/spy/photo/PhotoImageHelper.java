/*
 * Copyright (c) 1999 Dustin Sallings
 *
 * $Id: PhotoImageHelper.java,v 1.24 2003/07/26 08:38:27 dustin Exp $
 */

package net.spy.photo;

import net.spy.cache.SpyCache;

/**
 * Get images from the image server.
 */
public class PhotoImageHelper extends PhotoHelper { 

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
	public PhotoImage getImage(PhotoUser user, PhotoDimensions dim)
		throws Exception {

		PhotoSecurity.checkAccess(user, imageId);
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

			ImageServer server=ImageServerFactory.getImageServer();
			rv=server.getImage(imageId, dim);

			// If it's small enough, cache it.
			if(rv.size() < 32768) {
				cache.store(key, rv, 10*60*1000);
			}
		}

		return(rv);
	}

	/**
	 * Get the thumbnail for an image on behalf of a user.
	 */
	public PhotoImage getThumbnail(int uid) throws Exception {
		PhotoSecurity.checkAccess(uid, imageId);
		return(getThumbnail());
	}

	/**
	 * Get the thumbnail for an image.
	 */
	public PhotoImage getThumbnail() throws Exception {
		PhotoDimensions pdim=
			new PhotoDimensionsImpl(getConfig().get("thumbnail_size"));
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
			cache.store(key, rv, 1800000);
		}

		return(rv);
	}

	/**
	 * Store an image.
	 */
	public void storeImage(PhotoImage imageData) throws Exception {
		getLogger().info("Storing image " + imageId);
		ImageServer server=ImageServerFactory.getImageServer();
		server.storeImage(imageId, imageData);
		getLogger().info("Stored image " + imageId);
	}
}

/*
 * Copyright (c) 1999 Dustin Sallings
 *
 * $Id: PhotoImageHelper.java,v 1.19 2002/07/10 03:38:08 dustin Exp $
 */

package net.spy.photo;

import net.spy.cache.SpyCache;

/**
 * Get images from the image server.
 */
public class PhotoImageHelper extends PhotoHelper { 

	private int image_id=-1;
	private PhotoImage image_data=null;

	/**
	 * Get a PhotoHelper for the given image on behalf.
	 */
	public PhotoImageHelper(int which) throws Exception {
		super();
		this.image_id = which;
	}

	/**
	 * Get the full image on behalf of a user.
	 */
	public PhotoImage getImage(PhotoUser user, PhotoDimensions dim)
		throws Exception {

		PhotoSecurity.checkAccess(user, image_id);
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
		SpyCache cache=new SpyCache();

		StringBuffer keyb=new StringBuffer();
		keyb.append("img_");
		keyb.append(image_id);
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
			log("Cache miss on " + key);

			// Grab it from the server
			log("Grabbing " + key + " from image server");
			ImageServer server=ImageServerFactory.getImageServer();
			rv=server.getImage(image_id, dim);

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
		PhotoSecurity.checkAccess(uid, image_id);
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
	 * Store an image.
	 */
	public void storeImage(PhotoImage image_data) throws Exception {
		log("Storing image " + image_id);
		ImageServer server=ImageServerFactory.getImageServer();
		server.storeImage(image_id, image_data);
		log("Stored image " + image_id);
	}
}

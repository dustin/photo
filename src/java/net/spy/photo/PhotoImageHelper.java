// Copyright (c) 1999 Dustin Sallings

package net.spy.photo;

import java.lang.ref.SoftReference;

import net.spy.SpyObject;
import net.spy.cache.SimpleCache;
import net.spy.photo.impl.PhotoDimensionsImpl;
import net.spy.photo.util.ReferenceTracker;

/**
 * Get images from the image server.
 */
public class PhotoImageHelper extends SpyObject {

	private static final long CACHE_TIME = 10*60*1000;
	private static PhotoImageHelper instance=null;

	public static synchronized PhotoImageHelper getInstance() {
		if(instance == null) {
			instance=new PhotoImageHelper();
		}
		return instance;
	}

	/**
	 * Get the full image on behalf of a user.
	 */
	public byte[] getImage(User user, PhotoImage pid, PhotoDimensions dim)
		throws Exception {

		Persistent.getSecurity().checkAccess(user, pid.getId());
		return(getImage(pid, dim));
	}

	/**
	 * Get a full-size image.
	 */
	public byte[] getImage(PhotoImage pid) throws Exception {
		return(getImage(pid, null));
	}

	/**
	 * Get the full image.
	 */
	public byte[] getImage(PhotoImage pid, PhotoDimensions dim)
		throws Exception {

		byte[] rv=null;
		SimpleCache cache=SimpleCache.getInstance();

		StringBuilder keyb=new StringBuilder(64);
		keyb.append("img_");
		keyb.append(pid.getId());
		if(dim!=null) {
			keyb.append("_");
			keyb.append(dim.getWidth());
			keyb.append("x");
			keyb.append(dim.getHeight());
		}
		String key=keyb.toString();

		// Always check the cache first
		rv=(byte[])cache.get(key);
		if(rv==null) {
			if(getLogger().isDebugEnabled()) {
				getLogger().debug("Cache miss on " + key);
				// Grab it from the server
				getLogger().debug("Grabbing " + key + " from image server");
			}

			ImageServer server=Persistent.getImageServer();
			rv=server.getImage(pid, dim);

			// If it's small enough, cache it.
			if(rv.length < 32768) {
				cache.store(key, new SoftReference<byte[]>(rv), CACHE_TIME);
			}
		}

		ReferenceTracker.getInstance().addObject(rv);
		return(rv);
	}

	/**
	 * Get the thumbnail for an image on behalf of a user.
	 */
	public byte[] getThumbnail(PhotoImage pid, int uid) throws Exception {
		Persistent.getSecurity().checkAccess(uid, pid.getId());
		return(getThumbnail(pid));
	}

	/**
	 * Get the thumbnail for an image.
	 */
	public byte[] getThumbnail(PhotoImage pid) throws Exception {
		PhotoConfig cf=PhotoConfig.getInstance();
		PhotoDimensions pdim= new PhotoDimensionsImpl(cf.get("thumbnail_size"));
		return(getImage(pid, pdim));
	}

	/** 
	 * Get the size of this image as a thumbnail.
	 */
	public PhotoDimensions getThumbnailSize(PhotoImage pid)
		throws Exception {
		PhotoDimensions rv=null;
		SimpleCache cache=SimpleCache.getInstance();
		String key="ph_thumbnail_size_" + pid.getId();
		rv=(PhotoDimensions)cache.get(key);
		if(rv == null) {
			byte[] pi=getThumbnail(pid);
			PhotoParser.Result res=PhotoParser.getInstance().parseImage(pi);
			rv=new PhotoDimensionsImpl(res.getWidth(), res.getHeight());
			cache.store(key, new SoftReference<PhotoDimensions>(rv), 1800000);
		}

		return(rv);
	}
}

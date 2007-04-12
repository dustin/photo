// Copyright (c) 1999 Dustin Sallings <dustin@spy.net>

package net.spy.photo.impl;

import net.spy.SpyObject;
import net.spy.photo.ImageCache;
import net.spy.photo.ImageServer;
import net.spy.photo.ImageServerScaler;
import net.spy.photo.Instantiator;
import net.spy.photo.Persistent;
import net.spy.photo.PhotoConfig;
import net.spy.photo.PhotoDimensions;
import net.spy.photo.PhotoException;
import net.spy.photo.PhotoImage;
import net.spy.photo.PhotoParser;
import net.spy.photo.PhotoUtil;

/**
 * Base image server implementation.
 */
public class ImageServerImpl extends SpyObject implements ImageServer {

	private PhotoConfig conf = null;
	private ImageCache cache=null;
	private ImageServerScaler scaler=null;

	/**
	 * Get a ImageServerImpl using the given config.
	 */
	public ImageServerImpl() throws Exception {
		super();
		conf=PhotoConfig.getInstance();
		// Initialize the cache and scaler
		cache=new Instantiator<ImageCache>("imagecacheimpl",
				"net.spy.photo.LocalImageCacheImpl").getInstance();
		scaler=new Instantiator<ImageServerScaler>("scaler_class",
			"net.spy.photo.JavaImageServerScaler").getInstance();
	}

	/**
	 * @see ImageServer
	 */
	public byte[] getImage(PhotoImage pid, PhotoDimensions dim)
		throws PhotoException {
		return(getImage(pid, dim, true));
	}

	/**
	 * Get an image from the server with the option to bypass the cache for full
	 * size images.
	 * 
	 * @param imageId the image ID
	 * @param dim the dimensions at which you want the image
	 * @param withCache if true, use the cache, otherwise get it directly
	 */
	public byte[] getImage(PhotoImage pid, PhotoDimensions dim,
			boolean withCache)
		throws PhotoException {
		byte[] imageData=null;
		try {
			if(dim==null) {
				String key = "photo_" + pid.getId();
				if(withCache) {
					imageData = cache.getImage(key);
				}
				if(imageData == null) {
					getLogger().info("Couldn't find %s in cache", key);
					imageData=Persistent.getPermanentStorage().fetchImage(pid);
					assert imageData != null
						: "Could not retrieve " + pid
							+ " from permanent storage";
					if(withCache) {
						cache.putImage(key, imageData);
					}
				}
			} else {
				imageData=fetchScaledImage(pid, dim);
			}
		} catch(Exception e) {
			getLogger().warn("Error fetching image in %s",
					Persistent.getContextPath(), e);
			throw new PhotoException("Error fetching image", e);
		}
		return(imageData);
	}

	private byte[] fetchScaledImage(PhotoImage pid, PhotoDimensions dim)
		throws Exception {

		assert pid != null : "PhotoImage was null";

		// If there aren't dimensions, use the full dimensions.
		if(dim == null) {
			dim=pid.getDimensions();
		}

		byte[] pi=null;
		String key = "photo_" + pid.getId() + "_"
			+ dim.getWidth() + "x" + dim.getHeight();

		// Try cache first
		pi=cache.getImage(key);
		if(pi==null) {
			// Not in cache, get it
			pi=getImage(pid, null);
			PhotoParser.Result res=PhotoParser.getInstance().parseImage(pi);

			if(pi.equals(dim) || PhotoUtil.smallerThan(res, dim)) {
				getLogger().debug("Requested scaled size for " + pid
					+ "(" + dim + ") is equal to or "
					+ "greater than its full size, ignoring.");
			} else {
				// Scale it
				pi=scaleImage(pid, pi, dim);
				// Store it
				cache.putImage(key, pi);
				getLogger().info("Stored " + pid + " with key " + key);
			}
		}

		return(pi);
	}

	/**
	 * @see ImageServer
	 */
	public byte[] getThumbnail(PhotoImage pid) throws PhotoException {
		PhotoDimensions dim=new PhotoDimensionsImpl(conf.get("thumbnail_size"));
		return(getImage(pid, dim));
	}

	/**
	 * @see ImageServer
	 */
	public void storeImage(PhotoImage pid, byte[] image)
		throws PhotoException {
		try {
			String key="photo_" + pid.getId();
			if(cache.willStore(key, image)) {
				getLogger().info("Caching image %s with key %s",
						pid.getId(), key);
				cache.putImage(key, image);
			} else {
				getLogger().warn(
					"No cache will accept %s, going directly to permanent",
					key);
				Persistent.getPermanentStorage().storeImage(pid, image);
			}
		} catch(Exception e) {
			getLogger().warn("Error caching image", e);
			throw new PhotoException("Error storing image", e);
		}
	}

	private byte[] scaleImage(PhotoImage pid,
			byte[] in, PhotoDimensions dim) throws Exception {
		return(scaler.scaleImage(pid, in, dim));
	}

}

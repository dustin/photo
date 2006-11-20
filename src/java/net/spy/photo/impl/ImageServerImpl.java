// Copyright (c) 1999 Dustin Sallings <dustin@spy.net>
// arch-tag: DD71DEF4-5D6C-11D9-B761-000A957659CC

package net.spy.photo.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import net.spy.SpyObject;
import net.spy.db.SpyDB;
import net.spy.photo.ImageCache;
import net.spy.photo.ImageServer;
import net.spy.photo.ImageServerScaler;
import net.spy.photo.Instantiator;
import net.spy.photo.Persistent;
import net.spy.photo.PhotoConfig;
import net.spy.photo.PhotoDimensions;
import net.spy.photo.PhotoException;
import net.spy.photo.PhotoImage;
import net.spy.photo.PhotoImageData;
import net.spy.photo.PhotoUtil;
import net.spy.photo.observation.NewImageObservable;
import net.spy.util.Base64;

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
	public PhotoImage getImage(int imageId, PhotoDimensions dim)
		throws PhotoException {
		return(getImage(imageId, dim, true));
	}

	/**
	 * Get an image from the server with the option to bypass the cache for full
	 * size images.
	 * 
	 * @param imageId the image ID
	 * @param dim the dimensions at which you want the image
	 * @param withCache if true, use the cache, otherwise get it directly
	 */
	public PhotoImage getImage(int imageId, PhotoDimensions dim, boolean withCache)
		throws PhotoException {
		PhotoImage imageData=null;
		try {
			if(dim==null) {
				String key = "photo_" + imageId;
				if(withCache) {
					imageData = cache.getImage(key);
				}
				if(imageData == null) {
					imageData=fetchImageFromDB(imageId);
					if(withCache) {
						cache.putImage(key, imageData);
					}
				}
			} else {
				imageData=fetchScaledImage(imageId, dim);
			}
		} catch(Exception e) {
			getLogger().warn("Error fetching image in %s",
					Persistent.getContextPath(), e);
			throw new PhotoException("Error fetching image", e);
		}
		// Calculate the width
		imageData.getWidth();
		return(imageData);
	}

	private PhotoImage fetchScaledImage(int imageId, PhotoDimensions dim)
		throws Exception {

		PhotoImage pi=null;
		String key = "photo_" + imageId + "_"
			+ dim.getWidth() + "x" + dim.getHeight();

		// Try cache first
		pi=cache.getImage(key);
		if(pi==null) {
			// Not in cache, get it
			pi=getImage(imageId, null);

			if(pi.equals(dim) || PhotoUtil.smallerThan(pi, dim)) {
				getLogger().debug("Requested scaled size for " + imageId
					+ "(" + dim + ") is equal to or "
					+ "greater than its full size, ignoring.");
			} else {
				// Scale it
				pi=scaleImage(pi, dim);
				// Store it
				cache.putImage(key, pi);
				getLogger().info("Stored " + imageId + " with key " + key);
			}
		}

		return(pi);
	}

	/**
	 * @see ImageServer
	 */
	public PhotoImage getThumbnail(int imageId) throws PhotoException {
		PhotoDimensions dim=new PhotoDimensionsImpl(conf.get("thumbnail_size"));
		return(getImage(imageId, dim));
	}

	/**
	 * @see ImageServer
	 */
	public void storeImage(PhotoImageData pid, PhotoImage image)
		throws PhotoException {

		// Make sure we've calculated the width and height
		image.getWidth();
		try {
			cache.putImage("photo_" + pid.getId(), image);
		} catch(Exception e) {
			getLogger().warn("Error caching image", e);
			throw new PhotoException("Error storing image", e);
		}

		// Let everyone know there's a new image.
		NewImageObservable.getInstance().newImage(pid, image);
	}

	private PhotoImage scaleImage(
		PhotoImage in, PhotoDimensions dim) throws Exception {
		return(scaler.scaleImage(in, dim));
	}

	// Fetch an image from the DB
	private PhotoImage fetchImageFromDB(int imageId) throws Exception {
		// Average image is 512k.  Create a buffer of that size to start.
		StringBuffer sdata=new StringBuffer(512*1024);
		
		SpyDB db=new SpyDB(PhotoConfig.getInstance());
		String query="select data from image_store where id = ?\n"
			+ " order by line";
		PreparedStatement st = db.prepareStatement(query);
		st.setInt(1, imageId);
		ResultSet rs = st.executeQuery();
		
		while(rs.next()) {
			sdata.append(rs.getString("data"));
		}
		rs.close();
		db.close();
		
		Base64 base64 = new Base64();
		byte data[]=base64.decode(sdata.toString());
		PhotoImage pi=new PhotoImage(data);

		return(pi);
	}

}

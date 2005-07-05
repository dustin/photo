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
import net.spy.photo.PhotoConfig;
import net.spy.photo.PhotoDimensions;
import net.spy.photo.PhotoException;
import net.spy.photo.PhotoImage;
import net.spy.photo.PhotoUtil;
import net.spy.photo.util.PhotoStorerThread;
import net.spy.util.Base64;

/**
 * Base image server implementation.
 */
public class ImageServerImpl extends SpyObject implements ImageServer {

	private PhotoConfig conf = null;
	private ImageCache cache=null;
	private ImageServerScaler scaler=null;
	private static PhotoStorerThread storer=null;

	/**
	 * Get a ImageServerImpl using the given config.
	 */
	public ImageServerImpl() throws Exception {
		super();
		conf=PhotoConfig.getInstance();
		// Initialize the cache and scaler
		cache=initCache();
		scaler=initScaler();
		// Make sure the storer thread gets started immediately.
		checkStorerThread();
	}

	/**
	 * @see ImageServer
	 */
	public PhotoImage getImage(int imageId, PhotoDimensions dim)
		throws PhotoException {
		PhotoImage imageData=null;
		try {

			if(dim==null) {
				imageData=fetchImage(imageId);
			} else {
				imageData=fetchScaledImage(imageId, dim);
			}
		} catch(Exception e) {
			getLogger().warn("Error fetching image", e);
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
			pi=fetchImage(imageId);

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
	public void storeImage(int imageId, PhotoImage image)
		throws PhotoException {

		// Make sure we've calculated the width and height
		image.getWidth();
		try {
			cache.putImage("photo_" + imageId, image);
		} catch(Exception e) {
			getLogger().warn("Error caching image", e);
			throw new PhotoException("Error storing image", e);
		}

		// Let the storer thread know to wake up and start storing images.
		checkStorerThread();
		synchronized(storer) {
			storer.notifyAll();
		}
	}

	private static synchronized void checkStorerThread() {
		if(storer==null || !(storer.isAlive())) {
			storer=new PhotoStorerThread();
			storer.start();
		}
	}

	private PhotoImage scaleImage(
		PhotoImage in, PhotoDimensions dim) throws Exception {
		return(scaler.scaleImage(in, dim));
	}

	// Fetch an image
	private PhotoImage fetchImage(int imageId) throws Exception {
		String key=null;
		PhotoImage pi=null;

		key = "photo_" + imageId;

		pi=cache.getImage(key);

		if(pi==null) {
			// Average image is 512k.  Create a buffer of that size to start.
			StringBuffer sdata=new StringBuffer(512*1024);

			try {
				SpyDB db=new SpyDB(PhotoConfig.getInstance());
				String query="select * from image_store where id = ?\n"
					+ " order by line";
				PreparedStatement st = db.prepareStatement(query);
				st.setInt(1, imageId);
				ResultSet rs = st.executeQuery();

				while(rs.next()) {
					sdata.append(rs.getString("data"));
				}
				rs.close();
				db.close();

			} catch(Exception e) {
				getLogger().warn("Problem getting image", e);
				throw new Exception("Problem getting image", e);
			}

			// If we got an exception, throw it
			Base64 base64 = new Base64();
			byte data[]=base64.decode(sdata.toString());
			pi=new PhotoImage(data);
			cache.putImage(key, pi);
		}

		return(pi);
	}

	// Get a cache object server
	private ImageCache initCache() throws Exception {
		String classname=conf.get("imagecacheimpl",
			"net.spy.photo.LocalImageCacheImpl");
		getLogger().info("Initializing image cache:  " + classname);
		Class c=Class.forName(classname);
		return((ImageCache)c.newInstance());
	}
	private ImageServerScaler initScaler() throws Exception {
		String classname=conf.get("scaler_class",
			"net.spy.photo.JavaImageServerScaler");
		getLogger().info("Initializing image scaler:  " + classname);
		Class c=Class.forName(classname);
		ImageServerScaler iss=(ImageServerScaler)c.newInstance();
		iss.setConfig(conf);
		return(iss);
	}

}

// Copyright (c) 1999 Dustin Sallings <dustin@spy.net>
// $Id: ImageServerImpl.java,v 1.2 2002/06/17 03:52:32 dustin Exp $

package net.spy.photo;

import java.rmi.Naming;
import java.rmi.RemoteException;

import java.util.*;
import java.lang.*;
import java.io.*;
import java.sql.*;

import net.spy.*;
import net.spy.photo.*;
import net.spy.photo.util.*;
import net.spy.util.*;

/**
 * Implementation of the remote image server.
 */
public class ImageServerImpl extends Object implements ImageServer {

	private PhotoConfig conf = null;
	private ImageCache cache=null;
	private PhotoStorerThread storer=null;

	/**
	 * Get a ImageServerImpl using the given config.
	 */
	public ImageServerImpl() {
		super();
		conf=new PhotoConfig();
	}

	/**
	 * @see ImageServer
	 */
	public PhotoImage getImage(int image_id, PhotoDimensions dim)
		throws PhotoException {
		PhotoImage image_data=null;
		try {

			if(dim==null) {
				image_data=fetchImage(image_id);
			} else {
				image_data=fetchScaledImage(image_id, dim);
			}
		} catch(Exception e) {
			log("Error fetching image:  " + e);
			e.printStackTrace();
			throw new PhotoException("Error fetching image", e);
		}
		// Calculate the width
		image_data.getWidth();
		return(image_data);
	}

	private PhotoImage fetchScaledImage(int image_id, PhotoDimensions dim)
		throws Exception {

		PhotoImage pi=null;
		String key = "photo_" + image_id + "_"
			+ dim.getWidth() + "x" + dim.getHeight();

		// Try cache first
		getCache();
		pi=cache.getImage(key);
		if(pi==null) {
			// Not in cache, get it
			pi=fetchImage(image_id);

			if(pi.equals(dim) || pi.smallerThan(dim)) {
				log("Requested scaled size for " + image_id
					+ "(" + dim + ") is equal to or "
					+ " greater than its full size, ignoring.");
			} else {
				// Scale it
				pi=scaleImage(pi, dim);
				// Store it
				cache.putImage(key, pi);
				log("Stored " + image_id + " with key " + key);
			}
		}

		return(pi);
	}

	/**
	 * @see ImageServer
	 */
	public PhotoImage getThumbnail(int image_id) throws PhotoException {
		PhotoDimensions dim=new PhotoDimensionsImpl(conf.get("thumbnail_size"));
		return(getImage(image_id, dim));
	}

	/**
	 * @see ImageServer
	 */
	public void storeImage(int image_id, PhotoImage image)
		throws PhotoException {

		// Make sure we've calculated the width and height
		image.getWidth();
		try {
			getCache();
			cache.putImage("photo_" + image_id, image);
		} catch(Exception e) {
			log("Error caching image:  " + e);
			e.printStackTrace();
			throw new PhotoException("Error storing image", e);
		}

		// Let the storer thread know to wake up and start storing images.
		checkStorerThread();
		synchronized(storer) {
			storer.notify();
		}
	}

	private void checkStorerThread() {
		if(storer==null || !(storer.isAlive())) {
			System.err.println("Starting storer thread.");
			storer=new PhotoStorerThread();
			storer.start();
		}
	}

	private PhotoImage scaleImage(
		PhotoImage in, PhotoDimensions dim) throws Exception {
		
		Class c=Class.forName(conf.get("scaler_class",
			"net.spy.rmi.JavaImageServerScaler"));
		ImageServerScaler iss=(ImageServerScaler)c.newInstance();
		iss.setConfig(conf);
		return(iss.scaleImage(in, dim));
	}

	private void log(String what) {
		System.err.println(what);
	}

	// Fetch an image
	private PhotoImage fetchImage(int image_id) throws Exception {
		String key=null;
		PhotoImage pi=null;

		key = "photo_" + image_id;

		getCache();
		pi=cache.getImage(key);

		if(pi==null) {
			Connection photo=null;
			StringBuffer sdata=new StringBuffer();

			try {
				SpyDB db=new SpyDB(new PhotoConfig());
				String query="select * from image_store where id = ?\n"
					+ " order by line";
				PreparedStatement st = db.prepareStatement(query);
				st.setInt(1, image_id);
				ResultSet rs = st.executeQuery();

				while(rs.next()) {
					sdata.append(rs.getString("data"));
				}
				rs.close();
				db.close();

			} catch(Exception e) {
				log("Problem getting image:  " + e);
				e.printStackTrace();
				throw new Exception("Problem getting image: " + e);
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
	private void getCache() throws PhotoException {
		try {
			if(cache==null) {
				Class c=Class.forName(conf.get("imagecacheimpl",
					"net.spy.photo.LocalImageCacheImpl"));
				cache=(ImageCache)c.newInstance();
			}
		} catch(Exception e) {
			log("Error getting cache server");
			e.printStackTrace();
			cache=null;
			throw new PhotoException("Error getting cache server", e);
		}
	}
}

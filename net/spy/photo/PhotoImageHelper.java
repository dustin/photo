/*
 * Copyright (c) 1999 Dustin Sallings
 *
 * $Id: PhotoImageHelper.java,v 1.12 2002/02/21 20:44:27 dustin Exp $
 */

package net.spy.photo;

import java.io.*;
import java.util.*;
import java.rmi.Naming;

import net.spy.*;
import net.spy.cache.*;
import net.spy.rmi.*;

/**
 * Get images from the image server.
 */
public class PhotoImageHelper extends PhotoHelper
{ 
	private static ImageServer server = null;
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
	public PhotoImage getImage(int uid, PhotoDimensions dim) throws Exception {
		PhotoSecurity.checkAccess(uid, image_id);
		return(getImage(dim));
	}

	/**
	 * Get the full image.
	 */
	public PhotoImage getImage(PhotoDimensions dim)
		throws Exception {

		ensureConnected();
		PhotoImage rv=null;

		// Cache any images smaller than 320x200
		PhotoDimensions pdim=new PhotoDimensionsImpl("320x200");
		if(dim.equals(pdim) || dim.smallerThan(pdim)) {
			SpyCache cache=new SpyCache();
			String key="img_" + image_id + "_"
				+ dim.getWidth() + "x" + dim.getHeight();

			rv=(PhotoImage)cache.get(key);

			if(rv==null) {
				log("Cache miss on " + key);
				// Grab it from the server
				rv=server.getImage(image_id, dim);
				// Cache for ten minutes
				cache.store(key, rv, 10*60*1000);
			}
		} else {
			log("Getting image " + image_id + " from ImageServer");
			rv=server.getImage(image_id, dim);
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
		PhotoConfig conf=new PhotoConfig();
		PhotoDimensions pdim=
			new PhotoDimensionsImpl(conf.get("thumbnail_size"));
		return(getImage(pdim));
	}

	/**
	 * Store an image.
	 */
	public void storeImage(PhotoImage image_data) throws Exception {
		ensureConnected();
		log("Storing image " + image_id);
		server.storeImage(image_id, image_data);
		log("Stored image " + image_id);
	}

	// Make sure we're connected to an image server
	private synchronized void ensureConnected() throws Exception {
		boolean needconn=true;

		try {
			// If ping works, we don't need a new connection
			if(server.ping()) {
				needconn=false;
			}
		} catch(Exception e) {
			// nevermind
		}

		if(needconn) {
			log("Connecting to ImageServer");
			String serverpath=conf.get("imageserver");
			log("Locating " + serverpath);
			server=(ImageServer)Naming.lookup(serverpath);
			if(server==null) {
				throw new Exception("Can't get a server object");
			}
		}
	}
}

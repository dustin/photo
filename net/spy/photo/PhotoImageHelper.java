/*
 * Copyright (c) 1999 Dustin Sallings
 *
 * $Id: PhotoImageHelper.java,v 1.11 2002/02/20 11:32:12 dustin Exp $
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
		// log("Getting image " + image_id + " from ImageServer");
		return(server.getImage(image_id, dim));
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
		// Thumbnails are cachable.
		SpyCache cache=new SpyCache();
		String key="img_t_" + image_id;
		PhotoImage pi=(PhotoImage)cache.get(key);

		// If we didn't get it from our cache, get it from the image server's
		if(pi==null) {
			ensureConnected();
			/*
			log("Getting image "
				+ image_id + " (as thumbnail) from ImageServer");
			*/
			// Grab it from the image server.
			pi=server.getImage(image_id, true);
			// Cache the image for ten minutes.
			cache.store(key, pi, 10*60*1000);
		}
		return(pi);
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

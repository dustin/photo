/*
 * Copyright (c) 1999 Dustin Sallings
 *
 * $Id: PhotoImageHelper.java,v 1.7 2001/07/16 02:24:10 dustin Exp $
 */

package net.spy.photo;

import java.io.*;
import java.sql.*;
import java.util.*;
import java.rmi.Naming;

import net.spy.*;
import net.spy.db.*;
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

	// Verify the current logged-in user has access to this image.
	private void checkAccess(int uid) throws Exception {
		boolean ok=false;

		SpyCacheDB db=new SpyCacheDB(new PhotoConfig());
		PreparedStatement pst=db.prepareStatement(
			"select 1 from album a, wwwacl w\n"
			+ " where id=?\n"
			+ " and a.cat=w.cat\n"
			+ " and (w.userid=? or w.userid=?)\n", 900);
		pst.setInt(1, image_id);
		pst.setInt(2, uid);
		pst.setInt(3, PhotoUtil.getDefaultId());
		ResultSet rs=pst.executeQuery();

		// If there's a result, access is granted.
		if(rs.next()) {
			ok=true;
		}

		rs.close();
		pst.close();
		db.close();

		if(!ok) {
			throw new Exception("Access to this image is not allowed by user "
				+ uid);
		}
	}

	/**
	 * Get the full image on behalf of a user.
	 */
	public PhotoImage getImage(int uid) throws Exception {
		checkAccess(uid);
		return(getImage());
	}

	/**
	 * Get the full image.
	 */
	public PhotoImage getImage() throws Exception {
		ensureConnected();
		log("Getting image " + image_id + " from ImageServer");
		return(server.getImage(image_id, false));
	}

	/**
	 * Get the thumbnail for an image on behalf of a user.
	 */
	public PhotoImage getThumbnail(int uid) throws Exception {
		checkAccess(uid);
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
			log("Getting image "
				+ image_id + " (as thumbnail) from ImageServer");
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

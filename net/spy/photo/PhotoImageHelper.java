/*
 * Copyright (c) 1999 Dustin Sallings
 *
 * $Id: PhotoImageHelper.java,v 1.1 2000/06/30 04:11:19 dustin Exp $
 */

package net.spy.photo;

import java.io.*;
import java.sql.*;
import java.util.*;
import java.rmi.Naming;
import sun.misc.*;

import net.spy.*;
import net.spy.rmi.*;

// The class
public class PhotoImageHelper extends PhotoHelper
{ 
	protected static ImageServer server = null;
	protected int image_id=-1;
	PhotoImage image_data=null;

	public PhotoImageHelper(int which) throws Exception {
		super();
		image_id = which;
	}

	public PhotoImageHelper(int which, RHash r) throws Exception {
		super();
		image_id = which;
	}

	public void finalize() throws Throwable {
		super.finalize();
	}

	// Get an Image
	public PhotoImage getImage() throws Exception {
		ensureConnected();
		log("Getting image " + image_id + " from ImageServer");
		return(server.getImage(image_id, false));
	}

	// Get a thumbnail
	public PhotoImage getThumbnail() throws Exception {
		ensureConnected();
		log("Getting image " + image_id + " (as thumbnail) from ImageServer");
		return(server.getImage(image_id, true));
	}

	// Store an image
	public void storeImage(PhotoImage image_data) throws Exception {
		ensureConnected();
		log("Storing image " + image_id);
		server.storeImage(image_id, image_data);
		log("Stored image " + image_id);
	}

	// Make sure we're connected to an image server
	protected synchronized void ensureConnected() throws Exception {
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

// Copyright (c) 1999 Dustin Sallings <dustin@spy.net>
// $Id: ImageServerImpl.java,v 1.17 2002/03/01 22:06:08 dustin Exp $

package net.spy.rmi;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.RMISecurityManager;
import java.rmi.server.UnicastRemoteObject;

import java.util.*;
import java.lang.*;
import java.io.*;
import java.sql.*;

import net.spy.*;
import net.spy.photo.*;
import net.spy.photo.util.*;
import net.spy.util.*;

/**
 * Implementation of the image server.
 */
public class ImageServerImpl extends UnicastRemoteObject
	implements ImageServer {

	private RHash rhash=null;
	private SpyConfig conf = null;
	private boolean debug=false;

	private PhotoStorerThread storer=null;

	/**
	 * Get an ImageServerImpl using the given config.
	 */
	public ImageServerImpl(File config) throws RemoteException {
		super();
		conf=new SpyConfig(config);
		checkStorerThread();
	}

	/**
	 * @see ImageServer
	 */
	public PhotoImage getImage(int image_id, PhotoDimensions dim)
		throws RemoteException {
		PhotoImage image_data=null;
		debug("Requested image " + image_id + " at scale " + dim);
		try {

			if(dim==null) {
				image_data=fetchImage(image_id);
			} else {
				image_data=fetchScaledImage(image_id, dim);
			}
		} catch(Exception e) {
			log("Error fetching image:  " + e);
			e.printStackTrace();
			throw new RemoteException("Error fetching image", e);
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
		getRhash();
		pi=(PhotoImage)rhash.get(key);
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
				rhash.put(key, pi);
				log("Stored " + image_id + " with key " + key);
			}
		} else {
			debug("Found " + key + "(" + key.hashCode() + ") in cache.");
		}

		return(pi);
	}

	/**
	 * @see ImageServer
	 */
	public PhotoImage getImage(int image_id, boolean thumbnail)
		throws RemoteException {
		PhotoDimensions dim=null;
		PhotoConfig sconf=new PhotoConfig();
		if(thumbnail) {
			dim=new PhotoDimensionsImpl(sconf.get("thumbnail_size"));
		}
		return(getImage(image_id, dim));
	}

	/**
	 * @see ImageServer
	 */
	public void storeImage(int image_id, PhotoImage image)
		throws RemoteException {
		// Make sure we've calculated the width and height
		image.getWidth();
		getRhash();
		try {
			rhash.put("photo_" + image_id, image);
		} catch(Exception e) {
			log("Error caching image:  " + e);
			e.printStackTrace();
			throw new RemoteException("Error storing image", e);
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

	private void debug(String what) {
		if(debug) {
			System.err.println(what);
		}
	}

	// Fetch an image
	private PhotoImage fetchImage(int image_id) throws Exception {
		String key=null;
		PhotoImage pi=null;

		key = "photo_" + image_id;

		getRhash();
		pi=(PhotoImage)rhash.get(key);

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
			rhash.put(key, pi);
		} else {
			debug("Found " + key + " in cache.");
		}

		return(pi);
	}

	/**
	 * @see ImageServer
	 */
	public boolean ping() throws RemoteException {
		return(true);
	}

	// Get a cache object server
	private void getRhash() throws RemoteException {
		try {
			if(rhash==null) {
				rhash = new RHash(conf.get("rhash.url"));
			}
		} catch(Exception e) {
			log("Error getting RHash");
			e.printStackTrace();
			rhash=null;
			throw new RemoteException("Error getting RHash", e);
		}
	}

	/**
	 * Run it.
	 */
	public static void main(String args[]) throws Exception {
		if(args.length < 1) {
			System.err.println("imageserver.conf path not given.");
			throw new Exception("imageserver.conf path not given.");
		}
		ImageServerImpl i=new ImageServerImpl(new File(args[0]));
		Naming.rebind("ImageServer", i);
		System.err.println("ImageServer bound in registry");
	}
}

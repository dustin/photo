// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: ImageServerImpl.java,v 1.3 2002/07/10 04:00:17 dustin Exp $

package net.spy.photo.rmi;

import java.rmi.Naming;
import java.rmi.RemoteException;

import net.spy.photo.ImageServer;
import net.spy.photo.PhotoConfig;
import net.spy.photo.PhotoDimensions;
import net.spy.photo.PhotoDimensionsImpl;
import net.spy.photo.PhotoException;
import net.spy.photo.PhotoImage;

/**
 * Implementation of ImageServer as an RMI client.
 */
public class ImageServerImpl extends Object implements ImageServer {

	private static RemoteImageServer server = null;

	/**
	 * Get an instance of ImageServerImpl.
	 */
	public ImageServerImpl() {
		super();
	}

	/**
	 * @see ImageServer
	 */
	public PhotoImage getImage(int imageId, PhotoDimensions dim)
		throws PhotoException {

		PhotoImage rv=null;

		ensureConnected();

		try {
			rv=server.getImage(imageId, dim);
		} catch(RemoteException e) {
			throw new PhotoException("Error saving image.", e);
		}

		return(rv);
	}

	/**
	 * @see ImageServer
	 */
	public PhotoImage getThumbnail(int imageId) throws PhotoException {
		PhotoConfig conf=PhotoConfig.getInstance();
		PhotoDimensions pdim=
			new PhotoDimensionsImpl(conf.get("thumbnail_size"));
		return(getImage(imageId, pdim));
	}

	/**
	 * @see ImageServer
	 */
	public void storeImage(int imageId, PhotoImage image)
		throws PhotoException {

		ensureConnected();
		log("Storing image " + imageId);
		try {
			server.storeImage(imageId, image);
		} catch(RemoteException e) {
			throw new PhotoException("Error saving image", e);
		}
		log("Stored image " + imageId);
	}

	// Make sure the connection to the remote server is maintained.
	private static synchronized void ensureConnected() throws PhotoException {
		boolean needconn=true;

		try {
			if(server.ping()) {
				needconn=false;
			}
		} catch(Exception e) {
			// Need a new connection
		}

		if(needconn) {
			PhotoConfig conf=PhotoConfig.getInstance();
			String serverPath=conf.get("imageserver");
			log("Connecting to RemoteImageServer at " + serverPath);
			try {
				server=(RemoteImageServer)Naming.lookup(serverPath);
			} catch(Exception e) {
				throw new PhotoException(
					"Error getting RemoteImageServer from " + serverPath, e);
			}
		}
	}

	// log a message.
	private static void log(String msg) {
		System.err.println(msg);
	}

}

// Copyright (c) 1999 Dustin Sallings <dustin@spy.net>
// $Id: RemoteImageServerImpl.java,v 1.6 2002/07/10 03:38:08 dustin Exp $

package net.spy.photo.rmi;

import java.rmi.Naming;
import java.rmi.RemoteException;

import java.rmi.server.UnicastRemoteObject;

import net.spy.photo.PhotoDimensions;
import net.spy.photo.PhotoException;
import net.spy.photo.PhotoImage;
import net.spy.photo.ImageServer;

/**
 * Implementation of the remote image server.
 */
public class RemoteImageServerImpl extends UnicastRemoteObject
	implements RemoteImageServer {

	private boolean debug=false;

	/**
	 * Get a RemoteImageServerImpl.
	 */
	public RemoteImageServerImpl() throws RemoteException {
		super();
        // Let it initialize
        new net.spy.photo.ImageServerImpl();
	}

	private ImageServer getImageServer() {
		// Get a reference to the base image server implementation
		return(new net.spy.photo.ImageServerImpl());
	}

	/**
	 * @see RemoteImageServer
	 */
	public PhotoImage getImage(int image_id, PhotoDimensions dim)
		throws RemoteException {

		PhotoImage rv=null;

		try {
			ImageServer isi=getImageServer();
			rv=isi.getImage(image_id, dim);
		} catch(PhotoException e) {
			throw new RemoteException("Error getting image", e);
		}

		return(rv);
	}


	/**
	 * @see RemoteImageServer
	 */
	public PhotoImage getImage(int image_id, boolean thumbnail)
		throws RemoteException {

		PhotoImage rv=null;

		try {
			ImageServer isi=getImageServer();
			if(thumbnail) {
				rv=isi.getThumbnail(image_id);
			} else {
				rv=isi.getImage(image_id, null);
			}
		} catch(PhotoException e) {
			throw new RemoteException("Error getting image", e);
		}

		return(rv);
	}

	/**
	 * @see RemoteImageServer
	 */
	public void storeImage(int image_id, PhotoImage image)
		throws RemoteException {
		// Make sure we've calculated the width and height
		image.getWidth();
		try {
			ImageServer isi=getImageServer();
			isi.storeImage(image_id, image);
		} catch(PhotoException e) {
			log("Error caching image:  " + e);
			e.printStackTrace();
			throw new RemoteException("Error storing image", e);
		}
	}

	private void log(String what) {
		System.err.println(what);
	}

	private void debug(String what) {
		if(debug) {
			System.err.println(what);
		}
	}

	/**
	 * @see RemoteImageServer
	 */
	public boolean ping() throws RemoteException {
		return(true);
	}

	/**
	 * Run it.
	 */
	public static void main(String args[]) throws Exception {
		RemoteImageServerImpl i=new RemoteImageServerImpl();
		Naming.rebind("RemoteImageServer", i);
		System.err.println("RemoteImageServer bound in registry");
	}
}

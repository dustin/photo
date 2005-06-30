// Copyright (c) 1999 Dustin Sallings <dustin@spy.net>
// arch-tag: 9F9429F2-5D6D-11D9-8109-000A957659CC

package net.spy.photo.rmi;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import net.spy.photo.ImageServer;
import net.spy.photo.PhotoDimensions;
import net.spy.photo.PhotoException;
import net.spy.photo.PhotoImage;

/**
 * Implementation of the remote image server.
 */
public class RemoteImageServerImpl extends UnicastRemoteObject
	implements RemoteImageServer {

	private ImageServer server=null;

	private boolean debug=false;

	/**
	 * Get a RemoteImageServerImpl.
	 */
	public RemoteImageServerImpl() throws RemoteException {
		super();
        // Let it initialize
		try {
        	server=new net.spy.photo.impl.ImageServerImpl();
		} catch(Exception e) {
			throw new RemoteException("Problem initializing server", e);
		}
	}

	/**
	 * @see RemoteImageServer
	 */
	public PhotoImage getImage(int imageId, PhotoDimensions dim)
		throws RemoteException {

		PhotoImage rv=null;

		try {
			rv=server.getImage(imageId, dim);
		} catch(PhotoException e) {
			throw new RemoteException("Error getting image", e);
		}

		return(rv);
	}


	/**
	 * @see RemoteImageServer
	 */
	public PhotoImage getImage(int imageId, boolean thumbnail)
		throws RemoteException {

		PhotoImage rv=null;

		try {
			if(thumbnail) {
				rv=server.getThumbnail(imageId);
			} else {
				rv=server.getImage(imageId, null);
			}
		} catch(PhotoException e) {
			throw new RemoteException("Error getting image", e);
		}

		return(rv);
	}

	/**
	 * @see RemoteImageServer
	 */
	public void storeImage(int imageId, PhotoImage image)
		throws RemoteException {
		// Make sure we've calculated the width and height
		image.getWidth();
		try {
			server.storeImage(imageId, image);
		} catch(PhotoException e) {
			log("Error caching image:  " + e);
			e.printStackTrace();
			throw new RemoteException("Error storing image", e);
		}
	}

	private void log(String what) {
		System.err.println(what);
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

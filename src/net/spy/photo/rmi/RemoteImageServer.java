// Copyright (c) 1999 Dustin Sallings <dustin@spy.net>
// $Id: RemoteImageServer.java,v 1.3 2002/07/10 04:00:17 dustin Exp $

package net.spy.photo.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

import net.spy.photo.PhotoDimensions;
import net.spy.photo.PhotoImage;

/**
 * Interface of the ImageServer.
 */
public interface RemoteImageServer extends Remote { 
	/**
	 * Get an image by ID.
	 *
	 * @param imageId the image you want to get
	 * @param thumbnail if true, returns the image as a thumbnail
	 */
	PhotoImage getImage(int imageId, boolean thumbnail)
		throws RemoteException;

	/**
	 * Get an image by ID at a specific size.
	 *
	 * @param imageId the image you want to get
	 * @param dim the dimensions representing the max dimensions of the image
	 */
	PhotoImage getImage(int imageId, PhotoDimensions dim)
		throws RemoteException;

	/**
	 * Store the image by ID.
	 */
	void storeImage(int imageId, PhotoImage image) throws RemoteException;

	/**
	 * Determine if the remote end is still listening.
	 */
	boolean ping() throws RemoteException;
}

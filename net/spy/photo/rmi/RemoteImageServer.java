// Copyright (c) 1999 Dustin Sallings <dustin@spy.net>
// $Id: RemoteImageServer.java,v 1.2 2002/07/10 03:38:08 dustin Exp $

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
	 * @param image_id the image you want to get
	 * @param thumbnail if true, returns the image as a thumbnail
	 */
	PhotoImage getImage(int image_id, boolean thumbnail)
		throws RemoteException;

	/**
	 * Get an image by ID at a specific size.
	 *
	 * @param image_id the image you want to get
	 * @param dim the dimensions representing the max dimensions of the image
	 */
	PhotoImage getImage(int image_id, PhotoDimensions dim)
		throws RemoteException;

	/**
	 * Store the image by ID.
	 */
	void storeImage(int image_id, PhotoImage image) throws RemoteException;

	/**
	 * Determine if the remote end is still listening.
	 */
	boolean ping() throws RemoteException;
}

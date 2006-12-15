// Copyright (c) 1999 Dustin Sallings <dustin@spy.net>

package net.spy.photo.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

import net.spy.photo.PhotoDimensions;
import net.spy.photo.PhotoImage;
import net.spy.photo.PhotoImageData;

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
	void storeImage(PhotoImageData pid, PhotoImage image)
		throws RemoteException;

	/**
	 * Determine if the remote end is still listening.
	 */
	boolean ping() throws RemoteException;
}

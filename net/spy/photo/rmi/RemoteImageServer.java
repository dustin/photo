// Copyright (c) 1999 Dustin Sallings <dustin@spy.net>
// $Id: RemoteImageServer.java,v 1.1 2002/06/16 07:14:13 dustin Exp $

package net.spy.photo.rmi;

import java.rmi.Remote; 
import java.rmi.RemoteException; 

import java.util.*;
import net.spy.photo.*;

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
	PhotoImage getImage(int image_id, boolean thumbnail) throws RemoteException;

	/**
	 * Get an image by ID at a specific size.
	 *
	 * @param image_id the image you want to get
	 * @param dim the dimensions representing the max dimensions of the image
	 */
	PhotoImage getImage(int image_id, PhotoDimensions dim) throws RemoteException;

	/**
	 * Store the image by ID.
	 */
	void storeImage(int image_id, PhotoImage image) throws RemoteException;

	/**
	 * Determine if the remote end is still listening.
	 */
	boolean ping() throws RemoteException;
}

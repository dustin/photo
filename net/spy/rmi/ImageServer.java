// Copyright (c) 1999 Dustin Sallings <dustin@spy.net>
// $Id: ImageServer.java,v 1.3 2002/02/20 11:32:12 dustin Exp $

package net.spy.rmi;

import java.rmi.Remote; 
import java.rmi.RemoteException; 

import java.util.*;
import net.spy.photo.*;

/**
 * Interface of the ImageServer.
 */
public interface ImageServer extends Remote { 
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

// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: ImageServer.java,v 1.1 2002/06/16 08:09:13 dustin Exp $

package net.spy.photo;

/**
 * Any class wishing to provide images must implement this interface.
 */
public interface ImageServer {

	/**
	 * Get an image that will fit inside the given dimensions.
	 *
	 * @param image_id the ID of the image you want
	 * @param dim the dimensions representing the max dimensions of the image
	 *            if the dimensions are null, the image is returned at
	 *            maximum resolution
	 */
	PhotoImage getImage(int image_id, PhotoDimensions dim)
		throws PhotoException;

	/**
	 * Get a thumbnail at the default thumbnail size for the given image ID.
	 *
	 * @param image_id the ID of the image you want
	 */
	PhotoImage getThumbnail(int image_id) throws PhotoException;

	/**
	 * Store the given image.
	 */
	void storeImage(int image_id, PhotoImage image) throws PhotoException;

}

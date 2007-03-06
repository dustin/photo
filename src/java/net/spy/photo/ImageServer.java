// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>

package net.spy.photo;

/**
 * Any class wishing to provide images must implement this interface.
 */
public interface ImageServer {

	/**
	 * Get an image that will fit inside the given dimensions.
	 *
	 * @param imageId the ID of the image you want
	 * @param dim the dimensions representing the max dimensions of the image
	 *            if the dimensions are null, the image is returned at
	 *            maximum resolution
	 */
	byte[] getImage(PhotoImage pid, PhotoDimensions dim)
		throws PhotoException;

	/**
	 * Get a thumbnail at the default thumbnail size for the given image ID.
	 *
	 * @param imageId the ID of the image you want
	 */
	byte[] getThumbnail(PhotoImage pid) throws PhotoException;

	/**
	 * Store the given image.
	 */
	void storeImage(PhotoImage pid, byte[] image) throws PhotoException;

}

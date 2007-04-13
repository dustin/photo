package net.spy.photo;

import java.util.Collection;

/**
 * Permanent image storage for photos.
 */
public interface PermanentStorage {

	/**
	 * Initialize this storage.
	 */
	void init() throws Exception;

	/**
	 * Get a collection of image IDs that are known to exist, but not within
	 * this permanent storage instance.
	 *
	 * @return a collection of image IDs
	 * @throws Exception if there's a problem getting the collection of IDs
	 */
	Collection<Integer> getMissingIds() throws Exception;

	/**
	 * Permanently store the given image.
	 *
	 * @param pi the image descriptor
	 * @param data the image data itself
	 */
	void storeImage(PhotoImage pi, byte[] data) throws Exception;

	/**
	 * Fetch the data for the given image.
	 *
	 * @param pi the image descriptor
	 * @return the data for the image
	 */
	byte[] fetchImage(PhotoImage pi) throws Exception;
}

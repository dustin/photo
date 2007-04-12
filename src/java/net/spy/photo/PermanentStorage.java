package net.spy.photo;

/**
 * Permanent image storage for photos.
 */
public interface PermanentStorage {

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

// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>

package net.spy.photo;

/**
 * This interface is used to represent image cache implementations.
 */
public interface ImageCache {

	/**
	 * Return true if the cache is willing to store the given item.
	 *
	 * @param key the key
	 * @param image the data to store
	 * @return true if the cache things it can store the image
	 */
	boolean willStore(String key, byte[] image);

	/**
	 * Get an image by key.
	 */
	byte[] getImage(String key) throws PhotoException;

	/**
	 * Store an image by key.
	 */
	void putImage(String key, byte[] image) throws PhotoException;

}
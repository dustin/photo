// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
// arch-tag: D7EA087E-5D6C-11D9-8DD3-000A957659CC

package net.spy.photo;

/**
 * This interface is used to represent image cache implementations.
 */
public interface ImageCache {

	/**
	 * Get an image by key.
	 */
	PhotoImage getImage(String key) throws PhotoException;

	/**
	 * Store an image by key.
	 */
	void putImage(String key, PhotoImage image) throws PhotoException;

}



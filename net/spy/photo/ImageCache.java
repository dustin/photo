// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: ImageCache.java,v 1.2 2002/07/10 03:38:08 dustin Exp $

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



// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: PhotoDimensions.java,v 1.3 2002/07/10 03:38:08 dustin Exp $

package net.spy.photo;

/**
 * A width and height.
 */
public interface PhotoDimensions {

	/**
	 * Get the width of the set.
	 */
	int getWidth();
	/**
	 * Get the height of the set.
	 */
	int getHeight();

	/**
	 * Return true if this PhotoDimensions object is smaller than the one
	 * passed in.
	 */
	boolean smallerThan(PhotoDimensions pdim);

}



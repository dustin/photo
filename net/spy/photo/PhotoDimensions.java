// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: PhotoDimensions.java,v 1.2 2002/02/21 10:41:32 dustin Exp $

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

// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
// arch-tag: FBE3C4CA-5D6C-11D9-86A6-000A957659CC

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



// Copyright (c) 2005  Dustin Sallings <dustin@spy.net>

package net.spy.photo;

/**
 * A region of a photo.
 */
public interface PhotoRegion {

	/** 
	 * Get the starting X coordinate of the region.
	 */
	int getX();

	/** 
	 * Get the starting Y coordinate of the region.
	 */
	int getY();

	/** 
	 * Get the width of the region.
	 */
	int getWidth();

	/** 
	 * Get the height of the region.
	 */
	int getHeight();

}

// Copyright (c) 2006  Dustin Sallings <dustin@spy.net>
// arch-tag: 93355023-0F88-4491-9667-F45DB4FE10BC

package net.spy.photo;

/**
 * A location where a picture was taken.
 */
public interface Place extends Instance {

	/**
	 * Get the name of this location.
	 */
	String getName();

	/**
	 * Get the latitude of this location.
	 */
	double getLatitude();

	/**
	 * Get the longitude of this location.
	 */
	double getLongitude();
}

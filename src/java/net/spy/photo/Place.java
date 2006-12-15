// Copyright (c) 2006  Dustin Sallings <dustin@spy.net>

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

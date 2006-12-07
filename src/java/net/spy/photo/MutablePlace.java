// Copyright (c) 2006  Dustin Sallings <dustin@spy.net>
// arch-tag: B2D85843-BD01-450E-BCF1-38B38CAD85F9

package net.spy.photo;

/**
 * Mutable place.
 */
public interface MutablePlace extends Place {

	/**
	 * Set the name of this place.
	 */
	void setName(String to);

	/**
	 * Set the longitude of this place.
	 */
	void setLongitude(double to);

	/**
	 * Set the latitude of this place.
	 */
	void setLatitude(double to);
}

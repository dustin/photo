// Copyright (c) 2006  Dustin Sallings <dustin@spy.net>

package net.spy.photo.observation;

/**
 * An observation.
 */
public interface Observation<T> {

	/**
	 * The data that was observed.
	 */
	T getData();
}

// Copyright (c) 2006  Dustin Sallings <dustin@spy.net>
// arch-tag: B9C3F755-A079-4872-9760-715A68395DE4

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

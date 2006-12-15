// Copyright (c) 2006  Dustin Sallings <dustin@spy.net>

package net.spy.photo.observation;

/**
 * Something that listens for an observation.
 */
public interface Observer<T> {
	void observe(Observation<T> observation);
}

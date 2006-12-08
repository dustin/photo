// Copyright (c) 2006  Dustin Sallings <dustin@spy.net>
// arch-tag: 03BA60E4-7ABB-4ED4-80A1-A0A248DD4F1B

package net.spy.photo.observation;

/**
 * Something that listens for an observation.
 */
public interface Observer<T> {
	void observe(Observation<T> observation);
}

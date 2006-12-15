// Copyright (c) 2006  Dustin Sallings <dustin@spy.net>

package net.spy.photo.observation;

/**
 * Objects that are observable.
 */
public interface Observable<T> {

	void addObserver(Observer<T> l);

	void removeObserver(Observer<T> l);

	int getNumObservers();

	void removeAllObservers();

	void sendMessage(Observation<T> o);
}

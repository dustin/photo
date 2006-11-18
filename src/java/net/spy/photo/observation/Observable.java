// Copyright (c) 2006  Dustin Sallings <dustin@spy.net>
// arch-tag: 1F8FFA84-3059-4122-A1C3-E576ACE78532

package net.spy.photo.observation;

/**
 * Objects that are observable.
 */
public interface Observable<T> {

	void addObserver(Observer<T> l);

	void removeObserver(Observer<T> l);

	int getObserverCount();

	void removeAllObservers();

}

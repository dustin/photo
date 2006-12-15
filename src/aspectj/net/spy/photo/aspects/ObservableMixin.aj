// Copyright (c) 2006  Dustin Sallings <dustin@spy.net>

package net.spy.photo.aspects;

import java.util.Collection;
import java.util.concurrent.CopyOnWriteArraySet;

import net.spy.photo.observation.Observable;
import net.spy.photo.observation.Observation;
import net.spy.photo.observation.Observer;

/**
 * The observable implementation.
 */
public aspect ObservableMixin {

	private Collection<Observer<T>> Observable<T>.observers=
		new CopyOnWriteArraySet<Observer<T>>();

	public void Observable<T>.addObserver(Observer<T> l) {
		assert !observers.contains(l);
		observers.add(l);
		assert observers.contains(l);
	}

	public void Observable<T>.removeObserver(Observer<T> l) {
		observers.remove(l);
		assert !observers.contains(l);
	}

	public void Observable<T>.removeAllObservers() {
		observers.clear();
	}

	public int Observable<T>.getNumObservers() {
		return observers.size();
	}

	/**
	 * Deliver an observation to all registered listeners.
	 */
	public void Observable<T>.sendMessage(Observation<T> o) {
		for(Observer<T> l : observers) {
			l.observe(o);
		}
	}
}

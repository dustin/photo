// Copyright (c) 2006  Dustin Sallings <dustin@spy.net>
// arch-tag: AB390EAE-54A7-4534-99B6-8A3C406AA898

package net.spy.photo.observation;

import java.util.concurrent.LinkedBlockingQueue;

import net.spy.SpyObject;

/**
 * Base observable implementation.
 */
public abstract class BaseObservable<T> extends SpyObject
	implements Observable<T> {

	private LinkedBlockingQueue<Observer<T>> observers=
		new LinkedBlockingQueue<Observer<T>>();

	/* (non-Javadoc)
	 * @see net.spy.photo.observation.Observable#addObserver(net.spy.photo.observation.Observer)
	 */
	public void addObserver(Observer<T> l) {
		assert !observers.contains(l);
		observers.add(l);
		assert observers.contains(l);
	}

	/* (non-Javadoc)
	 * @see net.spy.photo.observation.Observable#removeObserver(net.spy.photo.observation.Observer)
	 */
	public void removeObserver(Observer<T> l) {
		observers.remove(l);
		assert !observers.contains(l);
	}

	/**
	 * Deliver an observation to all registered listeners.
	 */
	protected void sendMessage(Observation<T> o) {
		for(Observer<T> l : observers) {
			l.observe(this, o);
		}
	}

	/* (non-Javadoc)
	 * @see net.spy.photo.observation.Observable#getObserverCount()
	 */
	public int getObserverCount() {
		return observers.size();
	}

	/* (non-Javadoc)
	 * @see net.spy.photo.observation.Observable#removeAllObservers()
	 */
	public void removeAllObservers() {
		observers.clear();
	}
}

// Copyright (c) 2006  Dustin Sallings <dustin@spy.net>

package net.spy.photo.observation;

public class ObservationImpl<T> implements Observation<T> {

	private T ob=null;

	public ObservationImpl(T o) {
		super();
		ob=o;
	}

	public T getData() {
		return ob;
	}

}

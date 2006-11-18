// Copyright (c) 2006  Dustin Sallings <dustin@spy.net>
// arch-tag: 37D72B3B-8FEB-46CB-86F4-3580DD5EE2EA

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

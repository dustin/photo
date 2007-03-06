// Copyright (c) 2006  Dustin Sallings <dustin@spy.net>

package net.spy.photo.observation;

import net.spy.photo.PhotoImage;

/**
 * Observation sent when new images are added.
 */
public class NewImageObservable implements Observable<PhotoImage> {

	private static NewImageObservable instance=null;

	/**
	 * Get the singleton observer instance.
	 */
	public static synchronized NewImageObservable getInstance() {
		if(instance == null) {
			instance=new NewImageObservable();
		}
		return instance;
	}

	/**
	 * Report a new image was added.
	 * 
	 * @param pid the image meta data
	 */
	public void newImage(PhotoImage pid) {
		sendMessage(new ObservationImpl<PhotoImage>(pid));
	}
}

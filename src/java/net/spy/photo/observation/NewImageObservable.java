// Copyright (c) 2006  Dustin Sallings <dustin@spy.net>

package net.spy.photo.observation;

import net.spy.photo.PhotoImage;
import net.spy.photo.PhotoImageData;

/**
 * Observation sent when new images are added.
 */
public class NewImageObservable implements Observable<NewImageData> {

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
	 * @param pi the actual image data
	 */
	public void newImage(PhotoImageData pid, PhotoImage pi) {
		sendMessage(new ObservationImpl<NewImageData>(new NewImageData(pid)));
	}
}

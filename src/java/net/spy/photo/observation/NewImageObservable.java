// Copyright (c) 2006  Dustin Sallings <dustin@spy.net>
// arch-tag: E80E5CD1-3EB2-46F0-A1AC-6B6721E0DE64

package net.spy.photo.observation;

import net.spy.photo.PhotoImage;
import net.spy.photo.PhotoImageData;

/**
 * Observation sent when new images are added.
 */
public class NewImageObservable extends BaseObservable<NewImageData> {

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
		sendMessage(new ObservationImpl<NewImageData>(
				new NewImageData(pid, pi)));
	}
}

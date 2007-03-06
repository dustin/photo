// Copyright (c) 2006  Dustin Sallings <dustin@spy.net>

package net.spy.photo.aspects;

import net.spy.photo.ImageServer;
import net.spy.photo.PhotoImage;
import net.spy.photo.observation.NewImageObservable;

/**
 * Send notifications when new images are stored.
 */
public aspect StorerNotifications {

	pointcut storedImage(PhotoImage pid, byte[] image):
		execution(public void ImageServer.storeImage(PhotoImage, byte[]))
		&& args(pid, image);

	after(PhotoImage pid, byte[] image) returning:
		storedImage(pid, image) {

		// Let everyone know there's a new image.
		NewImageObservable.getInstance().newImage(pid, image);
	}
}

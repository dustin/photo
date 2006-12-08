// Copyright (c) 2006  Dustin Sallings <dustin@spy.net>
// arch-tag: 3631F35D-AD2B-4519-B594-0A2847FC0251

package net.spy.photo.aspects;

import net.spy.photo.PhotoImage;
import net.spy.photo.PhotoImageData;
import net.spy.photo.ImageServer;
import net.spy.photo.observation.NewImageObservable;

/**
 * Send notifications when new images are stored.
 */
public aspect StorerNotifications {

	pointcut storedImage(PhotoImageData pid, PhotoImage image):
		call(public void ImageServer.storeImage(PhotoImageData, PhotoImage))
		&& args(pid, image);

	after(PhotoImageData pid, PhotoImage image) returning:
		storedImage(pid, image) {

		// Let everyone know there's a new image.
		NewImageObservable.getInstance().newImage(pid, image);
	}
}

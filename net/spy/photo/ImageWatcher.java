// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: ImageWatcher.java,v 1.2 2002/07/10 03:38:08 dustin Exp $

package net.spy.photo;

import java.awt.Image;
import java.awt.Toolkit;

import java.awt.image.ImageObserver;

/**
 * Watches an image to wait for it to be fully loaded.
 */
public class ImageWatcher extends Object implements ImageObserver {

	private boolean isLoaded=false;
	private Image image=null;

	/**
	 * Get an instance of ImageWatcher to watch an image.
	 */
	public ImageWatcher(Image i) {
		super();
		this.image=i;
	}

	/**
	 * Wait for the image to be completely loaded.
	 */
	public void waitForImage() throws InterruptedException {
		if(!isLoaded) {
			Toolkit.getDefaultToolkit().prepareImage(image, -1, -1, this);
			synchronized(this) {
				wait();
			}
		}
	}

	/**
	 * Do this when image bits update.
	 */
	public boolean imageUpdate(Image img, int infoflags,
		int x, int y, int width, int height) {

		if( (infoflags&ALLBITS) != 0) {
			isLoaded=true;
			synchronized(this) {
				notify();
			}
		}

		return(!isLoaded);
	}

}

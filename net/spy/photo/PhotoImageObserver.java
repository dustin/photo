// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: PhotoImageObserver.java,v 1.3 2003/07/26 08:38:27 dustin Exp $

package net.spy.photo;

import java.awt.Image;
import java.awt.Toolkit;

import java.awt.image.ImageObserver;

import java.net.URL;

/**
 * Useful image observer.
 */
public class PhotoImageObserver extends Object implements ImageObserver {

	private boolean allbits=false;

	/**
	 * Get an instance of PhotoImageObserver.
	 */
	public PhotoImageObserver() {
		super();
	}

	/**
	 * Are all the bits ready?
	 */
	public boolean allBitsP() {
		return(allbits);
	}

	/**
	 * @see ImageObserver
	 */
	public synchronized boolean imageUpdate(Image i, int infoflags,
		int x, int y, int width, int height) {

		if( (infoflags&ALLBITS) != 0) {
			allbits=true;
			notifyAll();
		}
		return(!allbits);
	}

	// Prepare an image.
	private static Image prepareImage(Image i) {
		// Get an observer to use for the prepare
		PhotoImageObserver pio=new PhotoImageObserver();
		// Prepare it
		Toolkit.getDefaultToolkit().prepareImage(i, -1, -1, pio);
		// Wait
		if(!pio.allBitsP()) {
			synchronized(pio) {
				try {
					pio.wait(15000);
				} catch(InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		return(i);
	}

	/**
	 * Load an image, don't return until it's completely loaded.
	 */
	public static Image getImage(URL url) {
		return(prepareImage(Toolkit.getDefaultToolkit().getImage(url)));
	}

	/**
	 * Load an image, don't return until it's completely loaded.
	 */
	public static Image getImage(String filename) {
		return(prepareImage(Toolkit.getDefaultToolkit().getImage(filename)));
	}

}

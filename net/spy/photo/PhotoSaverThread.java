// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: PhotoSaverThread.java,v 1.1 2002/06/03 07:39:19 dustin Exp $

package net.spy.photo;

import java.util.Stack;
import java.util.EmptyStackException;

/**
 * Store images in the background.
 */
public class PhotoSaverThread extends Thread {

	private Stack jobQueue=null;
	private boolean going=true;

	private final static int SUCCESS=1;
	private final static int FAILURE=2;

	/**
	 * Get an instance of PhotoSaverThread.
	 */
	public PhotoSaverThread() {
		super("PhotoSaverThread");
		jobQueue=new Stack();
		start();
	}

	/**
	 * Tell the thread to stop running.
	 */
	public void stopRunning() {
		going=false;

		// Notify so we'll shut down immediately.
		synchronized(jobQueue) {
			jobQueue.notify();
		}
	}

	/**
	 * Add a new image to the queue.
	 */
	public void saveImage(PhotoSaver ps) {
		// Tell it to go to sleep.
		ps.passivate();
		jobQueue.push(ps);
		synchronized(jobQueue) {
			jobQueue.notify();
		}
	}

	// Report the status of the saving experience.
	private void report(int status, PhotoSaver ps) {
		if(status==SUCCESS) {
			System.out.println("Success storing " + ps);
		} else {
			System.out.println("FAILURE storing " + ps);
		}
	}

	/**
	 * Sit around and wait for new images to be stored.
	 */
	public void run() {
		while(going) {
			// Current job
			PhotoSaver ps=null;
			try {
				ps=(PhotoSaver)jobQueue.pop();
				System.out.println("Saving " + ps);
				ps.saveImage();
				report(SUCCESS, ps);
			} catch(EmptyStackException e) {
				try {
					synchronized(jobQueue) {
						// Wait up to an hour for something new.
						jobQueue.wait(3600000);
					}
				} catch(InterruptedException e2) {
					e2.printStackTrace();
					try {
						// wait five seconds on InterruptedException
						sleep(5000);
					} catch(InterruptedException e3) {
						e3.printStackTrace();
					}
				}
			} catch(PhotoException pe) {
				report(FAILURE, ps);
			}
		}

		System.out.println("PhotoSaverThread shutting down.");
	}

}

// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: PhotoSaverThread.java,v 1.5 2002/06/05 21:10:10 dustin Exp $

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

	private boolean processing=false;

	/**
	 * Get an instance of PhotoSaverThread.
	 */
	public PhotoSaverThread() {
		super("PhotoSaverThread");
		jobQueue=new Stack();
		start();
	}

	/**
	 * String me.
	 */
	public String toString() {
		StringBuffer sb=new StringBuffer();
		sb.append(super.toString());

		sb.append(" - Queue size:  ");
		sb.append(jobQueue.size());

		sb.append(" - ");
		if(!processing) {
			sb.append("NOT ");
		}
		sb.append("processing");

		return(sb.toString());
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
	private void report(int status, PhotoSaver ps, PhotoException e) {
		Mailer m=new Mailer();
		m.setTo(ps.getUser().getEmail());

		String body=null;
		String subject=null;
		if(status==SUCCESS) {
			subject="Success storing " + ps;
			body="\n\nThe storage of image " + ps.getId()
				+ " (keywords " + ps.getKeywords() + ") was successful.";
			System.out.println("Success storing " + ps);
		} else {
			subject="FAILURE storing " + ps;
			body="\n\nThe storage of image " + ps.getId()
				+ " (keywords " + ps.getKeywords() + ") has failed with the"
				+ " following exception:\n\n"
				+ e;
			System.out.println("FAILURE storing " + ps);
		}
		m.setSubject(subject);
		m.setBody(body);
		try {
			m.send();
		} catch(Exception e2) {
			e2.printStackTrace();
		}
	}

	/**
	 * Sit around and wait for new images to be stored.
	 */
	public void run() {
		while(going) {
			// We're not currently processing
			processing=false;
			// Current job
			PhotoSaver ps=null;
			try {
				ps=(PhotoSaver)jobQueue.pop();
				// Since we've popped the stack, mark us as processing.
				processing=true;
				System.out.println("Saving " + ps);
				ps.saveImage();
				report(SUCCESS, ps, null);
                System.out.println("Queue size:  " + jobQueue.size());
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
				report(FAILURE, ps, pe);
			}
		}

		System.out.println("PhotoSaverThread shutting down.");
	}

}

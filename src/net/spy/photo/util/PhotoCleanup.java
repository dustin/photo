/*
 * Copyright (c) 1999 Dustin Sallings
 *
 * $Id: PhotoCleanup.java,v 1.5 2002/07/10 03:38:09 dustin Exp $
 */

package net.spy.photo.util;

import net.spy.db.SpyDB;

import net.spy.photo.PhotoConfig;

/**
 * Run cleanup queries.
 */
public class PhotoCleanup extends Thread {

	/**
	 * Create the Cleaner.
	 */
	public PhotoCleanup() {
		super("photo_cleanup");
		this.setDaemon(true);
	}

	private void log(String msg) {
		System.err.println("PhotoCleanup:  " + msg);
	}

	private void cleanup() throws Exception {
		PhotoConfig p = PhotoConfig.getInstance();

		SpyDB db=new SpyDB(p);
		for(int i=1; p.get("cleaner.query" + i) != null; i++) {
			String query=p.get("cleaner.query" + i);
			log("Running the following query:\n" + query);
			db.executeUpdate(query);
			log("Finished query!");
		}
	}

	/**
	 * Run forever.
	 */
	public void run() {
		// Do a flush at the beginning, just in case stuff has been
		// building up.

		for(;;) {

			try {
				cleanup();
			} catch(Exception e1) {
				log("Cleaner error:  " + e1);
			}

			try {
				PhotoConfig p = PhotoConfig.getInstance();
				int m=p.getInt("cleaner_sleep", 1440);
				// Check every x minutes
				log("Sleeping for " + m + " minutes...");
				sleep(m * 60 * 1000);
			} catch(Exception e) {
				log("Error on cleanup sleep loop:  " + e);
			}
		}
	}

	/**
	 * Main for a standalone cleaner.
	 */
	public static void main(String args[]) {
		PhotoCleanup cleaner=new PhotoCleanup();
		cleaner.start();
	}
}

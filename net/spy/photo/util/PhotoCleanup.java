/*
 * Copyright (c) 1999 Dustin Sallings
 *
 * $Id: PhotoCleanup.java,v 1.3 2002/03/01 20:57:57 dustin Exp $
 */

package net.spy.photo.util;

import java.sql.*;
import java.lang.*;
import java.util.*;
import java.io.*;

import net.spy.*;
import net.spy.photo.*;

public class PhotoCleanup extends Thread {

	// Constructor
	public PhotoCleanup() {
		super("photo_cleanup");
		this.setDaemon(true);
	}

	private void log(String msg) {
		System.err.println("PhotoCleanup:  " + msg);
	}

	private void cleanup() throws Exception {
		PhotoConfig p = new PhotoConfig();

		SpyDB db=new SpyDB(p);
		for(int i=1; p.get("cleaner.query" + i) != null; i++) {
			String query=p.get("cleaner.query" + i);
			log("Running the following query:\n" + query);
			db.executeUpdate(query);
			log("Finished query!");
		}
	}

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
				PhotoConfig p = new PhotoConfig();
				int m=p.getInt("cleaner_sleep", 1440);
				// Check every x minutes
				log("Sleeping for " + m + " minutes...");
				sleep(m * 60 * 1000);
			} catch(Exception e) {
				log("Error on cleanup sleep loop:  " + e);
			}
		}
	}

	// In case it's run as its own little thingy.
	public static void main(String args[]) {
		PhotoCleanup cleaner=new PhotoCleanup();
		cleaner.start();
	}
}

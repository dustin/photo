/*
 * Copyright (c) 1999 Dustin Sallings
 *
 * $Id: PhotoLogFlusher.java,v 1.13 2002/03/01 10:22:36 dustin Exp $
 */

package net.spy.photo;

import java.sql.*;
import java.lang.*;
import java.util.*;
import java.io.*;

import net.spy.*;
import net.spy.log.*;
import net.spy.util.*;

/**
 * Flush logs.
 */
public class PhotoLogFlusher extends SpyLogFlusher {

	/**
	 * Get a log flusher.
	 */
	public PhotoLogFlusher() {
		super("PhotoLog");
		setPriority(NORM_PRIORITY-2);
	}

	/**
	 * Time to flush!
	 */
	protected void doFlush() throws Exception {
		Vector v = flush();
		// Only do all this crap if there's something to log.
		if(v.size() > 0) {
			Debug debug=new Debug("net.spy.photo.flush.debug");
			debug.debug("Flushing with " + v.size() + " items.");
			SpyDB photodb=null;
			try {
				photodb = new SpyDB(new PhotoConfig());
				Connection db=photodb.getConn();
				Statement st=db.createStatement();
				debug.debug("Beginning flush");
				for(Enumeration e=v.elements(); e.hasMoreElements();) {
					SpyLogEntry l=(SpyLogEntry)e.nextElement();
					debug.debug(l.toString());
					boolean tryagain=true;
					for(int i=0; i<3 && tryagain; i++) {
						if(i>0) {
							System.err.println("Attempt " + i);
						}
						try {
							st.executeUpdate(l.toString());
							tryagain=false;
						} catch(SQLException se) {
							// This kinda sucks, but we need this for
							// deadlock detection.
							String msg=se.getMessage();
							if(msg!=null
								&& msg.indexOf("Deadlock detected")>=0) {
								System.err.println(
									"Got deadlock trying to flush.");
								se.printStackTrace();
								try {
									sleep(5000);
								} catch(InterruptedException ise) {
									ise.printStackTrace();
								}
							} else {
								// Not a deadlock error, dump it, move on
								se.printStackTrace();
								tryagain=false;
							}
						} // Past SQL exception
					} // Retry loop on individual log entries.
				} // LogEntry loop
				st.close();
			} finally {
				photodb.close();
			}
			debug.debug("Flush complete.");
		}
	}
}

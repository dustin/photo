/*
 * Copyright (c) 1999 Dustin Sallings
 *
 * $Id: PhotoLogFlusher.java,v 1.12 2002/02/25 09:58:55 dustin Exp $
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

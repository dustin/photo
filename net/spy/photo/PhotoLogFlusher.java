/*
 * Copyright (c) 1999 Dustin Sallings
 *
 * $Id: PhotoLogFlusher.java,v 1.15 2002/04/01 08:18:32 dustin Exp $
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

	private boolean flushing=false;

	/**
	 * Get a log flusher.
	 */
	public PhotoLogFlusher() {
		super("PhotoLog");
		setPriority(NORM_PRIORITY-2);
	}

	/**
	 * Stringify.
	 */
	public String toString() {
		StringBuffer sb=new StringBuffer();
		sb.append(super.toString());

		if(flushing) {
			sb.append(" - currently flushing");
		}

		return(sb.toString());
	}

	/**
	 * Time to flush!
	 */
	protected void doFlush() throws Exception {
		Vector v = flush();
		// Just for sanity sake.
		flushing=false;
		// Only do all this crap if there's something to log.
		if(v.size() > 0) {
			flushing=true;
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
				flushing=false;
			}
			debug.debug("Flush complete.");
		} // Stuff to do
	} // Flush method
}

/*
 * Copyright (c) 1999 Dustin Sallings
 *
 * $Id: PhotoLogFlusher.java,v 1.19 2003/07/26 08:38:27 dustin Exp $
 */

package net.spy.photo;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.Enumeration;
import java.util.Vector;

import net.spy.SpyDB;

import net.spy.log.SpyLogEntry;
import net.spy.log.SpyLogFlusher;

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
		StringBuffer sb=new StringBuffer(128);
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
			if(getLogger().isDebugEnabled()) {
				getLogger().debug("Flushing with " + v.size() + " items.");
			}
			SpyDB photodb=null;
			try {
				photodb = new SpyDB(new PhotoConfig());
				Connection db=photodb.getConn();
				Statement st=db.createStatement();
				getLogger().debug("Beginning flush");
				for(Enumeration e=v.elements(); e.hasMoreElements();) {
					SpyLogEntry l=(SpyLogEntry)e.nextElement();
					if(getLogger().isDebugEnabled()) {
						getLogger().debug(l.toString());
					}
					boolean tryagain=true;
					for(int i=0; i<3 && tryagain; i++) {
						if(i>0) {
							getLogger().debug("Attempt " + i);
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
								getLogger().warn(
									"Got deadlock trying to flush.", se);
								try {
									sleep(5000);
								} catch(InterruptedException ise) {
									getLogger().warn("Interrupted waiting "
										+ "for deadlock retry", ise);
								}
							} else {
								// Not a deadlock error, dump it, move on
								getLogger().warn("Problem flushing log", se);
								tryagain=false;
							}
						} // Past SQL exception
					} // Retry loop on individual log entries.
				} // LogEntry loop
				st.close();
			} finally {
				if(photodb != null) {
					photodb.close();
				}
				flushing=false;
			}
			getLogger().debug("Flush complete.");
		} // Stuff to do
	} // Flush method
}

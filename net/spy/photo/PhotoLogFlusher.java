/*
 * Copyright (c) 1999 Dustin Sallings
 *
 * $Id: PhotoLogFlusher.java,v 1.10 2001/07/03 09:18:10 dustin Exp $
 */

package net.spy.photo;

import java.sql.*;
import java.lang.*;
import java.util.*;
import java.io.*;

import net.spy.*;
import net.spy.log.*;
import net.spy.util.*;

public class PhotoLogFlusher extends SpyLogFlusher {

	public PhotoLogFlusher() {
		super("PhotoLog");
	}

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
					st.executeUpdate(l.toString());
				}
				st.close();
			} finally {
				photodb.close();
			}
			debug.debug("Flush complete.");
		}
	}
}

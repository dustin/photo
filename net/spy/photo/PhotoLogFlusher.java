/*
 * Copyright (c) 1999 Dustin Sallings
 *
 * $Id: PhotoLogFlusher.java,v 1.8 2001/07/03 07:48:52 dustin Exp $
 */

package net.spy.photo;

import java.sql.*;
import java.lang.*;
import java.util.*;
import java.io.*;

import net.spy.*;
import net.spy.log.*;

public class PhotoLogFlusher extends SpyLogFlusher {

	public PhotoLogFlusher() {
		super("PhotoLog");
	}

	protected void doFlush() throws Exception {
		Vector v = flush();
		// Only do all this crap if there's something to log.
		if(v.size() > 0) {
			SpyDB photodb=null;
			try {
				photodb = new SpyDB(new PhotoConfig());
				Connection db=photodb.getConn();
				Statement st=db.createStatement();
				for(Enumeration e=v.elements(); e.hasMoreElements();) {
					SpyLogEntry l=(SpyLogEntry)e.nextElement();
					st.executeUpdate(l.toString());
				}
			} finally {
				photodb.close();
			}
		}
	}
}

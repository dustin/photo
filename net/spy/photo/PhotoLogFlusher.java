/*
 * Copyright (c) 1999 Dustin Sallings
 *
 * $Id: PhotoLogFlusher.java,v 1.7 2001/04/29 08:18:11 dustin Exp $
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

	public void doFlush() {
		Vector v = flush();
		Statement st = null;
		Connection db=null;
		SpyDB photodb=null;
		// Only do all this crap if there's something to log.
		if(v.size() > 0) {
			try {
				photodb = new SpyDB(new PhotoConfig());
				db=photodb.getConn();
				st=db.createStatement();
				for(int i = 0; i<v.size(); i++) {
					SpyLogEntry l = null;
					try {
						l = (SpyLogEntry)v.elementAt(i);
							st.executeUpdate(l.toString());
					} catch(SQLException e) {
						System.err.println("Error writing log:  "
							+ l + e.getMessage());
					}
				}
			} catch(Exception e) {
				System.err.println("BAD LOG ERRROR!  " + e.getMessage());
			} finally {
				photodb.freeDBConn();
			}
		}
	}
}

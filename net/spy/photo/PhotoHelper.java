/*
 * Copyright (c) 1999 Dustin Sallings
 *
 * $Id: PhotoHelper.java,v 1.3 2000/07/19 03:21:34 dustin Exp $
 */

package net.spy.photo;

import java.sql.*;
import java.util.*;
import java.lang.*;

import net.spy.*;
import net.spy.log.*;

// The class
public class PhotoHelper
{ 
	SpyLog logger;
	PhotoConfig conf;

	public PhotoHelper() throws Exception {
		super();
		initlog();
		conf = new PhotoConfig();
	}

	protected void initlog() {
		logger = new SpyLog(new PhotoLogFlusher());
	}

	protected void log(String message) {
		System.err.println("PhotoHelper: " + message);
	}

	// Grab a connection from the pool.
	protected synchronized Connection getDBConn() throws Exception {
		SpyDB pdb=new SpyDB(new PhotoConfig(), false);
		return(pdb.getConn());
	}

	// Gotta free the connection
	protected void freeDBConn(Connection conn) {
		SpyDB pdb=new SpyDB(new PhotoConfig(), false);
		pdb.freeDBConn(conn);
	}
}

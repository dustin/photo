/*
 * Copyright (c) 1999 Dustin Sallings
 *
 * $Id: PhotoServlet.java,v 1.17 2001/04/29 08:18:11 dustin Exp $
 */

package net.spy.photo;

import java.io.*;
import java.sql.*;
import java.util.*;
import java.net.*;
import sun.misc.*;

import javax.servlet.*;
import javax.servlet.http.*;

import net.spy.*;
import net.spy.log.*;
import net.spy.cache.*;

// The class
public class PhotoServlet extends HttpServlet
{ 
	// Only *really* persistent data can go here.
	public PhotoSecurity security = null;
	public PhotoAheadFetcher aheadfetcher=null;

	public SpyLog logger = null;

	protected SpyCache photoCache=null;

	protected PhotoLogFlusher logflusher=null;

	// The once only init thingy.
	public void init(ServletConfig config) throws ServletException {
		super.init(config);

		PhotoConfig conf = new PhotoConfig();

		// Security stuff
		try {
			log("Initing security");
			security = new PhotoSecurity();
			// Make sure we have initialized the guest user (and the
			// database and all that)
			log("Looking up guest.");
			security.getUser("guest");
			log("Finished security");
		} catch(Exception e) {
			throw new ServletException("Can't create security stuff:  " + e);
		}

		// The photo cache storer
		try {
			log("Initing PhotoCache");
			photoCache=new SpyCache();
			log("Got the photoCache");
		} catch(Exception e) {
			log("Could not get photoCache:  " + e);
			photoCache=null;
		}

		// The photo ahead fetcher
		try {
			log("Initing PhotoAheadFetcher");
			aheadfetcher=new PhotoAheadFetcher();
			log("Got the PhotoAheadFetcher");
		} catch(Exception e) {
			log("Could not get PhotoAheadFetcher:  " + e);
			aheadfetcher=null;
		}

		log("Initing logger");
		logflusher=new PhotoLogFlusher();
		logger = new SpyLog("PhotoLog", logflusher);
		log("got logger");
		log("Initialization complete");
	}

	// Shut the servlet down.
	public void destroy() {
		log("Stopping aheadfetcher");
		aheadfetcher.close();
		log("Removing logflusher");
		logger.removeFlusher(logflusher);
		log("Stopping logflusher");
		logflusher.close();
		log("Calling super destroy.");
		super.destroy();
	}

	// Servlet info
	public String getServletInfo() {
		return("Copyright (c) 2000  Dustin Sallings <dustin@spy.net>"
			+ " - $Revision: 1.17 $");
	}

	// Do a GET request
	public void doGet (
		HttpServletRequest request, HttpServletResponse response
	) throws ServletException, IOException {
		PhotoSession ps = new PhotoSession(this, request, response);
		ps.process();
	}

	// Do a POST request
	public void doPost (
		HttpServletRequest request, HttpServletResponse response
	) throws ServletException, IOException {

		PhotoSession ps = new PhotoSession(this, request, response);
		ps.process();
	}
}

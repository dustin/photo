/*
 * Copyright (c) 1999 Dustin Sallings
 *
 * $Id: PhotoServlet.java,v 1.9 2000/07/19 03:21:34 dustin Exp $
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

// The class
public class PhotoServlet extends HttpServlet
{ 
	// Only *really* persistent data can go here.
	public PhotoSecurity security = null;
	public PhotoAheadFetcher aheadfetcher=null;

	public SpyLog logger = null;

	protected PhotoCache photoCache=null;

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
			security.getUser("guest");
			log("Finished security");
		} catch(Exception e) {
			throw new ServletException("Can't create security stuff:  " + e);
		}

		// The photo cache storer
		try {
			log("Initing PhotoCache");
			photoCache=new PhotoCache();
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
		logger = new SpyLog(new PhotoLogFlusher());
		log("got logger");
		log("Initialization complete");
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

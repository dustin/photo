/*
 * Copyright (c) 1999 Dustin Sallings
 *
 * $Id: PhotoServlet.java,v 1.5 2000/07/05 01:03:41 dustin Exp $
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

// The class
public class PhotoServlet extends HttpServlet
{ 
	// Only *really* persistent data can go here.
	public String self_uri=null;
	public RHash rhash=null;
	public PhotoSecurity security = null;

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

		// Get an rhash to cache images and shite.
		try {
			log("Initing rhash");
			rhash = new RHash(conf.get("objectserver"));
			log("got rhash");
		} catch(Exception e) {
			log("Could not get rhash connection:  " + e);
			rhash = null;
		}

		try {
			log("Initing PhotoCache");
			photoCache=new PhotoCache();
			log("Got the photoCache");
		} catch(Exception e) {
			log("Could not get photoCache:  " + e);
			photoCache=null;
		}

		log("Initing logger");
		logger = new SpyLog(new PhotoLogFlusher());
		log("got logger");
		log("Initialization complete");
	}

	// Verify we have a valid rhash, if not, reopen it.
	protected void verify_rhash() {
		boolean needy=false;
		if(rhash==null || (!rhash.connected()) ) {
			log("Need a new rhash");
			try {
				// Try to reopen it
				PhotoConfig conf = new PhotoConfig();
				log("Getting rhash from " + conf.get("objectserver"));
				rhash = new RHash(conf.get("objectserver"));
				log("Got a new rhash");
			} catch(Exception e) {
				rhash=null;
				log("Error getting rhash:  " + e);
			}
		}
	}

	// Do a GET request
	public void doGet (
		HttpServletRequest request, HttpServletResponse response
	) throws ServletException, IOException {

		verify_rhash();
		PhotoSession ps = new PhotoSession(this, request, response);
		ps.process();
	}

	// Do a POST request
	public void doPost (
		HttpServletRequest request, HttpServletResponse response
	) throws ServletException, IOException {

		verify_rhash();
		PhotoSession ps = new PhotoSession(this, request, response);
		ps.process();
	}
}

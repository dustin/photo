// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: Persistent.java,v 1.1 2002/05/05 08:46:20 dustin Exp $

package net.spy.photo;

import javax.servlet.*;
import javax.servlet.http.*;

import net.spy.*;
import net.spy.log.*;

/**
 * All persistent objects will be available through this class.
 */
public class Persistent extends HttpServlet {

	public static PhotoSecurity security = null;
	public static PhotoAheadFetcher aheadfetcher=null;
	public static SpyLog logger = null;
	private PhotoLogFlusher logflusher=null;

	/**
	 * Get all of the persistent objects.
	 */
	public void init(ServletConfig config) throws ServletException {
		super.init(config);

		// This is pasted from PhotoServlet...I'm not sure which occurs
		// first and I'm being a bit lazy right now.  I think this will end
		// up being the permanent home.
		PhotoConfig conf = new PhotoConfig();
		// See if we need to provide a new configuration location
		String confpath=config.getInitParameter("configFile");
		if(confpath!=null) {
			// If it's in the WEB-INF, translate it to a filesystem path
			if(confpath.startsWith("/WEB-INF")) {
				confpath=config.getServletContext().getRealPath(confpath);
			}
			// Set the path
			conf.setStaticConfigLocation(confpath);
			// Get a new config for the changes to take effect
			conf=new PhotoConfig();
		}

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
			throw new ServletException("Can't create security stuff", e);
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

	/**
	 * Shut everything down.
	 */
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

}

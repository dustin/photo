// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: Persistent.java,v 1.7 2002/11/04 03:11:24 dustin Exp $

package net.spy.photo;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import javax.servlet.http.HttpServlet;

import net.spy.log.SpyLog;

/**
 * All persistent objects will be available through this class.
 */
public class Persistent extends HttpServlet {

	private static PhotoSecurity security = null;
	private static PhotoAheadFetcher aheadfetcher=null;
	private static SpyLog logger = null;
	private static PhotoSaverThread photoSaverThread=null;
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

		// initialize the category list early on, because it's simple DB
		// access and will look for no further data.
		try {
			log("Initializing categories.");
			Category.getAdminCatList();
		} catch(Exception e) {
			throw new ServletException("Can't initialize categories.", e);
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

		log("Initing PhotoSaverThread");
		photoSaverThread=new PhotoSaverThread();
		log("got PhotoSaverThread");

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
		log("Stopping PhotoSaverThread");
		photoSaverThread.stopRunning();
		log("Calling super destroy.");
		super.destroy();
	}

	/**
	 * Get the PhotoSecurity object for this instance.
	 */
	public static PhotoSecurity getSecurity() {
		if(security==null) {
			security=new PhotoSecurity();
		}
		return(security);
	}

	/**
	 * Get the PhotoAheadFetcher for this instance.
	 */
	public static PhotoAheadFetcher getAheadFetcher() {
		verifyInitialized();
		return(aheadfetcher);
	}

	/**
	 * Get the SpyLog object for this instance.
	 */
	public static SpyLog getLogger() {
		verifyInitialized();
		return(logger);
	}

	/**
	 * Get the PhotoSaverThread for this instance.
	 */
	public static PhotoSaverThread getPhotoSaverThread() {
		verifyInitialized();
		return(photoSaverThread);
	}

	private static void verifyInitialized() {
		if(security==null || aheadfetcher==null
			|| logger==null || photoSaverThread==null) {

			throw new NotInitializedException();
		}
	}

	/**
	 * This exception is thrown whenever methods are called from the
	 * Persistent servlet when it has not been initialized.
	 */
	public static class NotInitializedException extends RuntimeException {

		/**
		 * Get a new NotInitializedException with the default message.
		 */
		public NotInitializedException() {
			super("Persistent servlet was not properly initialized.  "
				+ "Container configuration problem?");
		}
	} // inner class for reporting initialization failures.

}

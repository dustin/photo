// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
// arch-tag: F09A30F4-5D6C-11D9-A69D-000A957659CC

package net.spy.photo;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import javax.servlet.http.HttpServlet;

import net.spy.db.TransactionPipeline;

import net.spy.photo.search.SavedSearch;

/**
 * All persistent objects will be available through this class.
 */
public class Persistent extends HttpServlet {

	private static PhotoSecurity security = null;
	private static TransactionPipeline pipeline = null;

	/**
	 * Get all of the persistent objects.
	 */
	public void init(ServletConfig config) throws ServletException {
		super.init(config);

		// This is pasted from PhotoServlet...I'm not sure which occurs
		// first and I'm being a bit lazy right now.  I think this will end
		// up being the permanent home.
		PhotoConfig conf = PhotoConfig.getInstance();
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
			conf=PhotoConfig.getInstance();
		}

		try {
			initStuff();
		} catch(Exception e) {
			throw new ServletException("Problem initting stuff", e);
		}
	}

	private void initStuff() throws Exception {
		// initialize the category list early on, because it's simple DB
		// access and will look for no further data.
		log("Initializing categories.");
		Category.getAdminCatList();

		// Security stuff
		log("Initing security");
		security = new PhotoSecurity();
		// Make sure we have initialized the guest user (and the
		// database and all that)
		log("Looking up guest.");
		security.getUser("guest");
		log("Finished security");

		log("Initializing TransactionPipeline");
		pipeline=new TransactionPipeline();
		log("got TransactionPipeline");

		log("Initializing image cache");
		PhotoImageDataFactory pidf=PhotoImageDataFactory.getInstance();
		pidf.recache();
		log("Image cache initialization complete");

		log("Initializing image server");
		ImageServerFactory.getImageServer();
		log("Image server initialization complete");

		log("Initializing searches cache");
		SavedSearch.getSearches();
		log("Saved searches initialization complete");

		log("Initializing properties cache");
		PhotoProperties photoProps=new PhotoProperties();
		log("Properties initialization complete");

		log("Initialization complete");
	}

	/**
	 * Shut everything down.
	 */
	public void destroy() {
		log("Shutting down transaction pipeline");
		pipeline.shutdown();
		log("pipeline shut down");
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
	 * Get the TransactionPipeline object for this instance.
	 */
	public static TransactionPipeline getPipeline() {
		verifyInitialized();
		return(pipeline);
	}

	private static void verifyInitialized() {
		if(security==null || pipeline==null) {
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

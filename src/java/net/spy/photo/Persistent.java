// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
// arch-tag: F09A30F4-5D6C-11D9-A69D-000A957659CC

package net.spy.photo;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import net.spy.SpyObject;
import net.spy.db.TransactionPipeline;
import net.spy.photo.search.SavedSearch;
import net.spy.photo.search.SearchCache;

/**
 * All persistent objects will be available through this class.
 */
public class Persistent extends SpyObject implements ServletContextListener {

	private static PhotoSecurity security = null;
	private static TransactionPipeline pipeline = null;


	public void contextInitialized(ServletContextEvent contextEvent) {
		ServletContext context=contextEvent.getServletContext();

		// This is pasted from PhotoServlet...I'm not sure which occurs
		// first and I'm being a bit lazy right now.  I think this will end
		// up being the permanent home.
		PhotoConfig conf = PhotoConfig.getInstance();
		// See if we need to provide a new configuration location
		String confpath=context.getInitParameter("net.spy.photo.config");
		if(confpath!=null) {
			// If it's in the WEB-INF, translate it to a filesystem path
			if(confpath.startsWith("/WEB-INF")) {
				confpath=context.getRealPath(confpath);
			}
			// Set the path
			conf.setStaticConfigLocation(confpath);
			// Get a new config for the changes to take effect
			conf=PhotoConfig.getInstance();
		} else {
			throw new RuntimeException("Couldn't find photo config path");
		}

		try {
			initStuff();
		} catch(Exception e) {
			throw new RuntimeException("Problem initting stuff", e);
		}
	}

	private void initStuff() throws Exception {
		// initialize the category list early on, because it's simple DB
		// access and will look for no further data.
		getLogger().info("Initializing categories.");
		CategoryFactory.getInstance().getAdminCatList();

		// Security stuff
		getLogger().info("Initing security");
		security = new PhotoSecurity();
		// Make sure we have initialized the guest user (and the
		// database and all that)
		getLogger().info("Looking up guest.");
		security.getUser("guest");
		getLogger().info("Finished security");

		getLogger().info("Initializing TransactionPipeline");
		pipeline=new TransactionPipeline();
		getLogger().info("got TransactionPipeline");

		getLogger().info("Initializing image cache");
		PhotoImageDataFactory pidf=PhotoImageDataFactory.getInstance();
		pidf.recache();
		getLogger().info("Image cache initialization complete");

		getLogger().info("Initializing image server");
		ImageServerFactory.getImageServer();
		getLogger().info("Image server initialization complete");

		getLogger().info("Initializing searches cache");
		SavedSearch.getSearches();
		getLogger().info("Saved searches initialization complete");

		getLogger().info("Initializing properties cache");
		PhotoProperties.getInstance();
		getLogger().info("Properties initialization complete");

		getLogger().info("Initializing search cache");
		SearchCache.getInstance();
		getLogger().info("Search cache initialization complete");

		getLogger().info("Initialization complete");
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
		return(pipeline);
	}

	public void contextDestroyed(ServletContextEvent contextEvent) {
		getLogger().info("Shutting down transaction pipeline");
		pipeline.shutdown();
		getLogger().info("pipeline shut down");
		getLogger().info("Shutting down search cache.");
		SearchCache.getInstance().shutdown();
		getLogger().info("Search cache shutdown complete");
		getLogger().info("Shutting down outstanding cache validations");
		CacheValidator.getInstance().cancelProcessing();
		getLogger().info("Cache validations shut down.");
	}

}

// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
// arch-tag: F09A30F4-5D6C-11D9-A69D-000A957659CC

package net.spy.photo;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import net.spy.SpyThread;
import net.spy.db.TransactionPipeline;
import net.spy.jwebkit.JWServletContextListener;
import net.spy.photo.search.ParallelSearch;
import net.spy.photo.search.SavedSearch;
import net.spy.photo.search.SearchCache;
import net.spy.photo.util.PhotoStorerThread;

/**
 * All persistent objects will be available through this class.
 */
public class Persistent extends JWServletContextListener {

	private static PhotoSecurity security = null;
	private static TransactionPipeline pipeline = null;
	private static ImageServer imageServer=null;
	private static PhotoStorerThread storer=null;

	public void ctxInit(ServletContextEvent contextEvent) throws Exception {
		ServletContext context=contextEvent.getServletContext();

		getLogger().info("Initializing photoservlet at "
			+ getContextPath(context));

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

		ParallelSearch.getInstance();

		// The initialization occurs in a different thread so all of those
		// threads can belong to the same thread group.
		ThreadGroup tg=new ThreadGroup(context.getServletContextName()
				+ " threads");
		tg.setDaemon(true);
		Init i=new Init(tg);
		i.start();
		i.join();
		if(i.getInitException() != null) {
			throw new RuntimeException("Problem initting stuff",
					i.getInitException());
		}
	}

	/**
	 * Get the PhotoSecurity object for this instance.
	 */
	public static PhotoSecurity getSecurity() {
		return(security);
	}

	/**
	 * Get the TransactionPipeline object for this instance.
	 */
	public static TransactionPipeline getPipeline() {
		return(pipeline);
	}

	/**
	 * Get the ImageServer instance.
	 */
	public static ImageServer getImageServer() {
		return(imageServer);
	}

	/**
	 * Get the photo storer thread.
	 */
	public static PhotoStorerThread getStorerThread() {
		return(storer);
	}

	public void ctxDestroy(ServletContextEvent contextEvent) {
		if(pipeline != null) {
			getLogger().info("Shutting down transaction pipeline");
			pipeline.shutdown();
			getLogger().info("pipeline shut down");
		}
		if(storer != null) {
			getLogger().info("Shutting down the storer thread.");
			storer.requestStop();
			getLogger().info("Storer thread shutdown complete.");
		}
		if(SearchCache.getInstance() != null) {
			getLogger().info("Shutting down search cache.");
			SearchCache.getInstance().shutdown();
			getLogger().info("Search cache shutdown complete");
		}
		if(CacheValidator.getInstance() != null
				&& CacheValidator.getInstance().isRunning()) {
			getLogger().info("Shutting down outstanding cache validations");
			CacheValidator.getInstance().cancelProcessing();
			getLogger().info("Cache validations shut down.");
		}
		ParallelSearch.setInstance(null);
	}


	private static class Init extends SpyThread {

		private Throwable initException;

		public Init(ThreadGroup tg) {
			super(tg, "Initializer initializer");
			setDaemon(true);
		}

		public Throwable getInitException() {
			return initException;
		}

		public void run() {
			try {
				initStuff();
			} catch(Throwable t) {
				initException=t;
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
			pipeline=new TransactionPipeline(getThreadGroup(), null);
			getLogger().info("got TransactionPipeline");

			getLogger().info("Initializing search cache");
			SearchCache.setup();
			getLogger().info("Search cache initialization complete");

			getLogger().info("Initializing image cache");
			PhotoImageDataFactory pidf=PhotoImageDataFactory.getInstance();
			pidf.recache();
			getLogger().info("Image cache initialization complete");

			getLogger().info("Initializing image server");
			imageServer=new Instantiator<ImageServer>("imageserverimpl",
			"net.spy.photo.impl.ImageServerImpl").getInstance();
			getLogger().info("Image server initialization complete");

			getLogger().info("Initializing searches cache");
			SavedSearch.getSearches();
			getLogger().info("Saved searches initialization complete");

			getLogger().info("Initializing properties cache");
			PhotoProperties.getInstance();
			getLogger().info("Properties initialization complete");

			getLogger().info("Initializing the photo storer thread.");
			storer=new PhotoStorerThread();
			storer.start();
			getLogger().info("PhotoStorerThread initialization complete.");

			getLogger().info("Initialization complete");
		}
	}

}

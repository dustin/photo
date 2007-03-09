// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>

package net.spy.photo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.naming.NamingException;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import net.spy.SpyThread;
import net.spy.db.TransactionPipeline;
import net.spy.jwebkit.JWServletContextListener;
import net.spy.photo.jms.ImageMessageConsumer;
import net.spy.photo.jms.ImageMessagePoster;
import net.spy.photo.mail.MailPoller;
import net.spy.photo.observation.NewImageObservable;
import net.spy.photo.search.ParallelSearch;
import net.spy.photo.search.SavedSearch;
import net.spy.photo.search.SearchCache;
import net.spy.photo.util.PhotoStorerThread;

/**
 * All persistent objects will be available through this class.
 */
public class Persistent extends JWServletContextListener {

	static PhotoSecurity security = null;
	static TransactionPipeline pipeline = null;
	static ImageServer imageServer=null;
	static PhotoStorerThread storer=null;
	static ScheduledExecutorService executor=null;

	private static Collection<ShutdownHook> shutdownHooks;
	private static String contextPath;

	@Override
	public void ctxInit(ServletContextEvent contextEvent) throws Exception {
		ServletContext context=contextEvent.getServletContext();

		contextPath=getContextPath(context);

		shutdownHooks=new ArrayList<ShutdownHook>();

		getLogger().info("Initializing photoservlet at " + contextPath);

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

		addShutdownHook(ParallelSearch.getInstance());

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

		// Initialize the observers
		NewImageObservable.getInstance().addObserver(
				Persistent.getStorerThread());
		try {
			addShutdownHook(new ImageMessageConsumer());

			ImageMessagePoster imp = new ImageMessagePoster();
			NewImageObservable.getInstance().addObserver(imp);
			addShutdownHook(imp);
		} catch(Exception e) {
			getLogger().info("Couldn't initialize JMS queue for new images", e);
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

	/**
	 * Get the configured context path for this instance.
	 */
	public static String getContextPath() {
		assert contextPath != null;
		return contextPath;
	}

	/**
	 * Get the timer for scheduling stuff.
	 */
	public static ScheduledExecutorService getExecutor() {
		assert executor != null;
		return executor;
	}

	/**
	 * Add a shutdown hook.
	 */
	public static void addShutdownHook(ShutdownHook sh) {
		shutdownHooks.add(sh);
	}

	@Override
	public void ctxDestroy(ServletContextEvent contextEvent) throws Exception {
		if(pipeline != null) {
			getLogger().info("Shutting down transaction pipeline");
			pipeline.shutdown();
			getLogger().info("pipeline shut down");
		}

		if(executor != null) {
			List<Runnable> notrunning=executor.shutdownNow();
			if(notrunning.size() > 0) {
				getLogger().warn("Not running these tasks:  %s", notrunning);
			}
			executor=null;
		}

		for(ShutdownHook sh : shutdownHooks) {
			getLogger().info("Calling shutdown hook:  %s", sh);
			try {
				sh.onShutdown();
			} catch(Throwable e) {
				getLogger().error("Error on shutdown hook %s", sh, e);
			}
		}
		shutdownHooks.clear();
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

		@Override
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
			PhotoImageFactory pidf=PhotoImageFactory.getInstance();
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
			addShutdownHook(storer);
			getLogger().info("PhotoStorerThread initialization complete.");


			getLogger().info("Initializing S3Service");
			try {
				S3Service s3s=S3Service.getInstance();
				s3s.init();
				NewImageObservable.getInstance().addObserver(s3s);
			} catch(NamingException e) {
				getLogger().info("Couldn't initialize S3 stuff", e);
			}
			getLogger().info("S3Service initialization complete");

			getLogger().info("Initializing mail stuff");
			PhotoConfig conf=PhotoConfig.getInstance();
			String mailName=conf.get("mailJNDIName");
			executor=new ScheduledThreadPoolExecutor(1);
			if(mailName != null) {
				executor.scheduleWithFixedDelay(new MailPoller(mailName),
						5, 30, TimeUnit.SECONDS);
			}
			getLogger().info("Mail stuff initialization complete");

			getLogger().info("Initialization complete");
		}
	}

}

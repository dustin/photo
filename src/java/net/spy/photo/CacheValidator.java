// Copyright (c) 2005  Dustin Sallings <dustin@spy.net>

package net.spy.photo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import net.spy.SpyObject;
import net.spy.SpyThread;
import net.spy.photo.impl.ImageServerImpl;
import net.spy.photo.impl.PhotoDimensionsImpl;
import net.spy.photo.search.ParallelSearch;
import net.spy.photo.search.SearchResults;
import net.spy.photo.struts.SearchForm;
import net.spy.util.RingBuffer;
import net.spy.xml.SAXAble;
import net.spy.xml.XMLUtils;

/**
 * Cache validation and build-out utility.
 */
public class CacheValidator extends SpyObject implements SAXAble, ShutdownHook {

	private static CacheValidator instance=null;

	private RunThread runThread=null;
	private int runs=0;

	private CacheValidator() {
		super();
	}

	/**
	 * Get the singleton instance.
	 */
	public static synchronized CacheValidator getInstance() {
		if(instance == null) {
			instance=new CacheValidator();
			Persistent.addShutdownHook(instance);
		}
		return(instance);
	}

	/**
	 * Begin processing the given operations.
	 * 
	 * @param operations the given operations
	 * @param user the user who will be processing the operations
	 * @exception IllegalStateException if the processing is already running
	 */
	public synchronized void process(Collection<Operation> operations,
			User user) {
		if(isRunning()) {
			throw new IllegalStateException(
					"Can't begin processing while already processing");
		}
		runThread=new RunThread(operations, user);
		runThread.start();
		runs++;
	}

	/**
	 * Process all validators.
	 */
	public void process(User user) {
		Collection<Operation> ops=new ArrayList<Operation>();
		ops.add(new ValidateCacheAndDB());
		ops.add(new ValidateMD5s());
		ops.add(new RecacheThumbnailsAndSizes());
		process(ops, user);
	}

	/**
	 * Get the number of times runs have been started.
	 */
	public int getRuns() {
		return(runs);
	}

	/**
	 * Cancel any pending processing.
	 */
	public synchronized void cancelProcessing() {
		if(isRunning()) {
			getLogger().info("Cancelling cache validation.");
			runThread.stopRequested=true;
			runThread.interrupt();
		}
	}

	/**
	 * Are we running a validation?
	 */
	public synchronized boolean isRunning() {
		return(runThread != null && runThread.isAlive());
	}

	/**
	 * XML me.
	 */
	public void writeXml(ContentHandler h) throws SAXException {
		XMLUtils x=XMLUtils.getInstance();
		x.startElement(h, "cachevalidation");
		x.doElement(h, "runs", String.valueOf(runs));
		x.doElement(h, "running", String.valueOf(isRunning()));
		if(runThread != null) {
			x.doElement(h, "todo", String.valueOf(runThread.todo));
			x.doElement(h, "done", String.valueOf(runThread.done));
			x.startElement(h, "errors");
			synchronized(runThread.errs) {
				for(String e : runThread.errs) {
					x.doElement(h, "error", e);
				}
			}
			x.endElement(h, "errors");
		}
		x.endElement(h, "cachevalidation");
	}

	public void onShutdown() throws Exception {
		cancelProcessing();
	}

	/**
	 * Get the thread that is running, or most recently ran validation.
	 */
	public synchronized RunThread getRunThread() {
		return(runThread);
	}

	public static class RunThread extends SpyThread {
		Collection<Operation> operations=null;
		Collection<String> errs=null;
		User user=null;

		int todo=0;
		int done=0;

		volatile boolean stopRequested=false;

		public RunThread(Collection<Operation> ops, User u) {
			super("CacheValidatorThread");
			setDaemon(true);
			operations=ops;
			user=u;
			errs=Collections.synchronizedCollection(new RingBuffer<String>(10));
		}

		@Override
		public void run() {
			SearchForm sf=new SearchForm();
			SearchResults psr=null;
			try {
				psr=ParallelSearch.getInstance().performSearch(sf, user);
			} catch (Throwable e) {
				getLogger().warn(
					"Could not perform search for cache validation", e);
				errs.add("Could not perform search for cache validation " + e);
			}
			if(psr != null) {
				try {
					getLogger().info("Beginning validation of %d images",
							psr.getSize());
					runValidation(psr);
				} catch(Throwable t) {
					getLogger().warn("Error performing cache validation", t);
					errs.add("Error performing cache validation: " + t);
				}
			}
		}

		private void runValidation(SearchResults sr) {
			todo=sr.getSize();
			done=0;
			for(PhotoImage pid : sr.getAllObjects()) {
				for(Operation op : operations) {
					try {
						op.process(pid, errs);
					} catch (Exception e) {
						getLogger().warn("Exception on on " + op + " in "
								+ pid.getId(), e);
						errs.add("Exception on " + op + " in " + pid.getId()
								+ ": " + e);
					}
				}
				done++;
				if(stopRequested) {
					getLogger().info(
							"User requested cache validation to stop.");
					break;
				}
			}
		}

		public Collection<Operation> getOperations() {
			return(Collections.unmodifiableCollection(operations));
		}

		public int getDone() {
			return done;
		}

		public Collection<String> getErrs() {
			return errs;
		}

		public int getTodo() {
			return todo;
		}

		public User getUser() {
			return user;
		}

		@Override
		public synchronized String toString() {
			return super.toString() + " - processing " + (done+1) + " of " + todo;
		}
	}

	/**
	 * Validate an individual image.
	 */
	public static interface Operation {
		void process(PhotoImage img, Collection<String> errs)
			throws Exception;
	}

	/**
	 * Validate the cached data and the DB data are consistent.  If the cache is
	 * empty for this record, then fill it.
	 */
	public static class ValidateCacheAndDB extends SpyObject
		implements Operation {

		public void process(PhotoImage img,
				Collection<String> errs) throws Exception {
			ImageServer is=Persistent.getImageServer();
			if(is instanceof ImageServerImpl) {
				ImageServerImpl isi=(ImageServerImpl)is;
				byte[] fromDB=isi.getImage(img, null, false);
				byte[] fromCache=isi.getImage(img, null, true);
				if(!Arrays.equals(fromDB, fromCache)) {
					errs.add("Didn't get the same result from cache and DB for "
							+ img.getId());
				}
			} else {
				errs.add("Can't validate " + img.getId()
						+ " because server is not ImageServerImpl");
			}
		}
	}

	public static class ValidateMD5s extends SpyObject implements Operation {

		public void process(PhotoImage img, Collection<String> errs)
			throws Exception {
			byte[] pi=Persistent.getImageServer().getImage(
					img, null);
			PhotoParser.Result res=PhotoParser.getInstance().parseImage(pi);
			String storedMd5=img.getMd5();
			assert storedMd5 != null : "No MD5 for image " + img.getId();
			if(!storedMd5.equals(res.getMd5())) {
				errs.add("MD5 is incorrect for img " + img.getId());
			}
		}
		
	}

	public static class RecacheThumbnailsAndSizes extends SpyObject
		implements Operation {

		private Collection <PhotoDimensions> sizes=null;

		public RecacheThumbnailsAndSizes(Collection<PhotoDimensions> s) {
			super();
			sizes=s;
		}

		public RecacheThumbnailsAndSizes() {
			super();
			sizes=new ArrayList<PhotoDimensions>();
			sizes.add(new PhotoDimensionsImpl("800x600"));	
		}

		public void process(PhotoImage img,
				Collection<String> errs) throws Exception {
			ImageServer is=Persistent.getImageServer();
			is.getThumbnail(img);
			for(PhotoDimensions dim : sizes) {
				is.getImage(img, dim);
			}
		}
		
	}
}

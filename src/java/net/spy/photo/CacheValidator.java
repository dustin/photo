// Copyright (c) 2005  Dustin Sallings <dustin@spy.net>
// arch-tag: FCE5E70C-BF9D-422F-B5F4-D7B20F36566C

package net.spy.photo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import net.spy.SpyObject;
import net.spy.SpyThread;
import net.spy.jwebkit.SAXAble;
import net.spy.jwebkit.XMLUtils;
import net.spy.photo.impl.ImageServerImpl;
import net.spy.photo.impl.PhotoDimensionsImpl;
import net.spy.photo.search.Search;
import net.spy.photo.search.SearchResults;
import net.spy.photo.struts.SearchForm;
import net.spy.util.RingBuffer;

/**
 * Cache validation and build-out utility.
 */
public class CacheValidator extends SpyObject implements SAXAble {

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

	/**
	 * Get the thread that is running, or most recently ran validation.
	 */
	public synchronized RunThread getRunThread() {
		return(runThread);
	}

	public static class RunThread extends SpyThread {
		private Collection<Operation> operations=null;
		private Collection<String> errs=null;
		private User user=null;

		private int todo=0;
		private int done=0;

		boolean stopRequested=false;

		public RunThread(Collection<Operation> ops, User u) {
			super("CacheValidatorThread");
			setDaemon(true);
			operations=ops;
			user=u;
			errs=Collections.synchronizedCollection(new RingBuffer<String>(10));
		}

		public void run() {
			SearchForm sf=new SearchForm();
			Search ps=Search.getInstance();
			try {
				SearchResults psr=ps.performSearch(sf, user);
				runValidation(psr);
			} catch (Exception e) {
				getLogger().warn(
					"Could not perform search for cache validation", e);
				errs.add("Could not perform search for cache validation " + e);
			}
		}

		private void runValidation(SearchResults sr) {
			todo=sr.size();
			done=0;
			for(PhotoImageData pid : sr) {
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
				if(interrupted() || stopRequested) {
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

		public Collection getErrs() {
			return errs;
		}

		public int getTodo() {
			return todo;
		}

		public User getUser() {
			return user;
		}

		public synchronized String toString() {
			return super.toString() + " - processing " + (done+1) + " of " + todo;
		}
	}

	/**
	 * Validate an individual image.
	 */
	public static interface Operation {
		void process(PhotoImageData img, Collection<String> errs)
			throws Exception;
	}

	/**
	 * Validate the cached data and the DB data are consistent.  If the cache is
	 * empty for this record, then fill it.
	 */
	public static class ValidateCacheAndDB extends SpyObject
		implements Operation {

		public void process(PhotoImageData img,
				Collection<String> errs) throws Exception {
			ImageServer is=Persistent.getImageServer();
			if(is instanceof ImageServerImpl) {
				ImageServerImpl isi=(ImageServerImpl)is;
				PhotoImage fromDB=isi.getImage(img.getId(), null, false);
				PhotoImage fromCache=isi.getImage(img.getId(), null, true);
				if(!Arrays.equals(fromDB.getData(), fromCache.getData())) {
					errs.add("Didn't get the same result from cache and DB for "
							+ img.getId());
				}
			} else {
				errs.add("Can't validate " + img.getId()
						+ " because server is not ImageServerImpl");
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
			sizes.add(new PhotoDimensionsImpl("50x50"));
			sizes.add(new PhotoDimensionsImpl("800x600"));	
		}

		public void process(PhotoImageData img,
				Collection<String> errs) throws Exception {
			ImageServer is=Persistent.getImageServer();
			is.getThumbnail(img.getId());
			for(PhotoDimensions dim : sizes) {
				is.getImage(img.getId(), dim);
			}
		}
		
	}
}

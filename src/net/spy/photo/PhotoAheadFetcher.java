/*
 * Copyright (c) 1999 Dustin Sallings
 *
 * $Id: PhotoAheadFetcher.java,v 1.20 2003/07/26 08:38:27 dustin Exp $
 */

package net.spy.photo;

import net.spy.SpyObject;
import net.spy.util.ThreadPool;

/**
 * This object fetches ahead in resultsets as to speed up the appearance of
 * the photo album.
 */
public class PhotoAheadFetcher extends SpyObject {
	private ThreadPool tp=null;

	/**
	 * Get a new PhotoheadFetcher (auto starts).
	 */
	public PhotoAheadFetcher() {
		super(); // thanks for asking

		tp=new ThreadPool("PhotoAheadFetcher", 3, Thread.NORM_PRIORITY-1);
	}

	/**
	 * Tell the Aheadfetcher to stop soon.
	 */
	public void close() {
		tp.shutdown();
	}

	/**
	 * Add the PhotoSearchResults to the ahead-fetching queue.
	 */
	public void next(PhotoSearchResults r) {
		tp.addTask(new FetchAhead(r));
	}

	// This class uses the thread pool to fetch ahead.
	private static class FetchAhead extends SpyObject implements Runnable {

		private PhotoSearchResults results=null;

		public FetchAhead(PhotoSearchResults r) {
			super();
			this.results=r;
		}

		/**
	 	 * Do the actual updating here.
	 	 */
		public void run() {
			try {
				// Give it a second to finish the page.
				Thread.sleep(5000);
				fetchAhead();
			} catch(Exception e) {
				getLogger().warn("Problem fetching", e);
			}
		}

		// We got a notify, fetch ahead.
		private void fetchAhead() throws Exception {
			PhotoSearchResults r=results;

			// OK, we're not going to use the normal next() method on the
			// resultset because we don't want to modify the result set.

			int current=r.current();
			// Loop on the result set using our own counter.
			for(int i=0; i<r.getMaxRet() && (current+i)<r.size(); i++) {
				PhotoImageData res=null;
				res=(PhotoImageData)r.get(current+i);
				if(res!=null) {
					// This will cache the thumbnails
					PhotoImageHelper p=new PhotoImageHelper(res.getId());
					getLogger().debug("PhotoAheadFetcher fetching "
						+ res.getId());
					p.getThumbnail();
				} // Got a result
			} // end for loop on a given result set
		}

	}

}

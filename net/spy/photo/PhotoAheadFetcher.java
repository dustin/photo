/*
 * Copyright (c) 1999 Dustin Sallings
 *
 * $Id: PhotoAheadFetcher.java,v 1.15 2002/03/05 04:32:23 dustin Exp $
 */

package net.spy.photo;

import java.util.*;

import net.spy.util.*;

/**
 * This object fetches ahead in resultsets as to speed up the appearance of
 * the photo album.
 */
public class PhotoAheadFetcher extends Object {
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
	private class FetchAhead extends Object implements Runnable {

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
				e.printStackTrace();
			}
		}

		// We got a notify, fetch ahead.
		private void fetchAhead() throws Exception {
			PhotoSearchResults r=results;
			String self_uri=r.getURI();

			// OK, we're not going to use the normal next() method on the
			// resultset because we don't want to modify the result set.

			int current=r.current();
			// Loop on the result set using our own counter.
			for(int i=0; i<r.getMaxRet() && (current+i)<r.nResults(); i++) {
				PhotoSearchResult res=null;
				res=(PhotoSearchResult)r.get(current+i);
				if(res!=null) {
				 	Hashtable h=new Hashtable();
				 	// Populate the data thingies.
				 	res.addToHash(h);
				 	res.showXML(self_uri);

				 	// This will cache the thumbnails
				 	int image_id=Integer.parseInt((String)h.get("IMAGE"));
				 	PhotoImageHelper p=new PhotoImageHelper(image_id);
					System.out.println("PhotoAheadFetcher fetching "
						+ image_id);
				 	p.getThumbnail();
				} // Got a result
			} // end for loop on a given result set
		}

	}

}

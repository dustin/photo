/*
 * Copyright (c) 1999 Dustin Sallings
 *
 * $Id: PhotoAheadFetcher.java,v 1.1 2000/07/05 06:08:33 dustin Exp $
 */

package net.spy.photo;

import java.util.*;

/**
 * This object fetches ahead in resultsets as to speed up the appearance of
 * the photo album.
 */
public class PhotoAheadFetcher extends Thread {
	protected static Vector sets=null;

	public PhotoAheadFetcher() {
		super(); // thanks for asking
		this.setDaemon(true);
		this.start();
	}

	public void run() {
		// Create the sets vector.
		sets=new Vector();
		while(true) {
			try {
				synchronized(sets) {
					sets.wait();
				}
				fetchAhead();
			} catch(Exception e) {
				System.err.println("PhotoAheadFetcher error:  " + e);
				e.printStackTrace();
				try {
					// Sleep a second on failure.
					sleep(1000);
				} catch(Exception e2) {
					// Don't care, it'll just go faster.
				}
			}
		}
	}

	// Add to the queue.
	public void next(PhotoSearchResults r) {
		synchronized(sets) {
			sets.addElement(r);
			sets.notify();
		}
	}

	// We got a notify, fetch ahead.
	protected void fetchAhead() {
		Vector tofetch=new Vector();

		// In a lock, copy the current queue into a temporary one, and
		// empty out the queue.
		synchronized(sets) {
			for(Enumeration e=sets.elements(); e.hasMoreElements();) {
				tofetch.addElement(e.nextElement());
			}
			sets.removeAllElements();
		}

		// Outside of the lock, fetch (precache) the entries.
		for(Enumeration e=tofetch.elements(); e.hasMoreElements();) {
			PhotoSearchResults r=(PhotoSearchResults)e.nextElement();
			for(int i=0; i<5; i++) {
				PhotoSearchResult res=r.next();
				if(res!=null) {
					 res.addToHash(null);
				}
			} // end one resultset
		} // end resultsets

	}

}

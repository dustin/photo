/*
 * Copyright (c) 1999 Dustin Sallings
 *
 * $Id: PhotoAheadFetcher.java,v 1.8 2000/11/10 07:17:18 dustin Exp $
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
		setDaemon(true);
		setName("PhotoAheadFetcher");
		start();
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
	protected void fetchAhead() throws Exception {
		Vector tofetch=new Vector();

		// In a lock, copy the current queue into a temporary one, and
		// empty out the queue.
		synchronized(sets) {
			for(Enumeration e=sets.elements(); e.hasMoreElements();) {
				tofetch.addElement(e.nextElement());
			}
			sets.removeAllElements();
		}

		// System.out.println("Need to fetch ahead in the following:\n"
		// 	+ tofetch);

		// Outside of the lock, fetch (precache) the entries.
		for(Enumeration e=tofetch.elements(); e.hasMoreElements();) {
			PhotoSearchResults r=(PhotoSearchResults)e.nextElement();
			String self_uri=r.getURI();

			for(int i=0; i<5; i++) {
				PhotoSearchResult res=null;
				// Synchronize on the result thingy so that the display
				// doesn't get weird if the client is moving too fast.
				synchronized(r) {
					res=r.next();
				}
				if(res!=null) {
				 	Hashtable h=new Hashtable();
				 	// Populate the data thingies.
				 	res.addToHash(h);
				 	res.showXML(self_uri);

				 	// This will cache the thumbnails
				 	int image_id=Integer.parseInt((String)h.get("IMAGE"));
				 	PhotoImageHelper p =
						new PhotoImageHelper(image_id);
				 	p.getThumbnail();
				}
			} // end one resultset
		} // end resultsets

	}

}

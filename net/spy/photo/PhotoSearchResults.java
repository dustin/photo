/*
 * Copyright (c) 2000  Dustin Sallings <dustin@spy.net>
 *
 * $Id: PhotoSearchResults.java,v 1.15 2002/05/21 07:45:09 dustin Exp $
 */

package net.spy.photo;

import java.util.*;
import java.io.Serializable;

import net.spy.*;

/**
 * Represents results from a search.
 */
public class PhotoSearchResults extends Cursor {
	private String self_uri=null;

	private PhotoDimensions maxSize=null;

	/**
	 * Get a search results object for the given URI.
	 */
	public PhotoSearchResults(String self_uri) {
		super();
		this.self_uri=self_uri;
	}

	/**
	 * Add a search result to the list.
	 */
	public void add(PhotoImageData d) {
		// Set the result id
		d.setSearchId(nResults());
		// Tell it the size we want the images
		d.setMaxDims(maxSize);
		// Now add it
		addElement(d);
	}

	/**
	 * Set the maximum image size to be represented.
	 */
	public void setMaxSize(PhotoDimensions maxSize) {
		this.maxSize=maxSize;
	}

	/**
	 * Add a photo ID to the result list.
	 */
	public void add(Integer what) {
		addElement(what);
	}

	/**
	 * Get the URI this result was built from.
	 */
	public String getURI() {
		return(self_uri);
	}

	/**
	 * String representation of the search results.
	 */
	public String toString() {
		return("Photo search results " + super.toString());
	}

	/**
	 * Get the entry at the given location.
	 */
	public Object get(int which) {
		PhotoImageData ret=null;
		Object o=super.get(which);
		// We hope that it's a PhotoSearchResult, but an Integer will do.
		try {
			ret=(PhotoImageData)o;
		} catch(ClassCastException e) {
			try {
				// Synchronize on the object in this position.
				synchronized(o) {
					Integer i=(Integer)o;
					ret=PhotoImageData.getData(i.intValue(), maxSize);
					// Set the search ID.
					ret.setSearchId(which);
					// Next time, won't need to do this again.
					replace(which, ret);
				}
			} catch(Exception e2) {
				e2.printStackTrace();
				System.err.println("Error getting result "
					+ which + " from " + o + ":  " + e2);
			}
		}
		return(ret);
	}

}

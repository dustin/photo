/*
 * Copyright (c) 2000  Dustin Sallings <dustin@spy.net>
 *
 * $Id: PhotoSearchResults.java,v 1.11 2002/02/23 23:14:01 dustin Exp $
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
	public void add(PhotoSearchResult r) {
		// Set the result id
		r.setId(nResults());
		// Tell it the size we want the images
		r.setMaxSize(maxSize);
		// Now add it
		addElement(r);
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
		PhotoSearchResult ret=null;
		Object o=super.get(which);
		// We hope that it's a PhotoSearchResult, but an Integer will do.
		try {
			ret=(PhotoSearchResult)o;
		} catch(ClassCastException e) {
			try {
				Integer i=(Integer)o;
				ret=new PhotoSearchResult(i.intValue(), which);
				// Add the max dimensions so it'll scale.
				ret.setMaxSize(maxSize);
				// Next time, won't need to do this again.
				replace(which, ret);
			} catch(Exception e2) {
				System.err.println("Error getting result "
					+ which + ":  " + e2);
			}
		}
		return(ret);
	}

}

/*
 * Copyright (c) 2000  Dustin Sallings <dustin@spy.net>
 *
 * $Id: PhotoSearchResults.java,v 1.19 2002/07/10 03:38:08 dustin Exp $
 */

package net.spy.photo;

/**
 * Represents results from a search.
 */
public class PhotoSearchResults extends Cursor {

	private PhotoDimensions maxSize=null;

	/**
	 * Get a search results object.
	 */
	public PhotoSearchResults() {
		super();
	}

	/**
	 * Add a search result to the list.
	 */
	public void add(PhotoImageData d) {
		// Set the result id
		d.setSearchId(size());
		// Tell it the size we want the images
		d.setMaxDims(maxSize);
		// Now add it
		super.add(d);
	}

	/**
	 * Set the maximum image size to be represented.
	 */
	public void setMaxSize(PhotoDimensions maxSize) {
		this.maxSize=maxSize;
	}

	/**
	 * Get the maximum image size to be represented.
	 */
	public PhotoDimensions getMaxSize() {
		return(maxSize);
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
					set(which, ret);
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


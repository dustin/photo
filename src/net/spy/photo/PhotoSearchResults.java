/*
 * Copyright (c) 2000  Dustin Sallings <dustin@spy.net>
 *
 * $Id: PhotoSearchResults.java,v 1.20 2003/05/04 08:19:29 dustin Exp $
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
	 * Get a search results object with an initial capacity.
	 */
	public PhotoSearchResults(int capacity) {
		super(capacity);
	}

	/**
	 * Add a search result to the list.
	 */
	public void add(PhotoImageData d) {
		super.add(new PhotoSearchResult(d, size()));
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
		PhotoSearchResult ret=null;
		Object o=super.get(which);
		// We hope that it's a PhotoSearchResult, but an Integer will do.
		try {
			ret=(PhotoSearchResult)o;
		} catch(ClassCastException e) {
			try {
				// Synchronize on the object in this position.
				synchronized(o) {
					Integer i=(Integer)o;
					PhotoImageData pid=PhotoImageDataImpl.getData(i.intValue());
					ret=new PhotoSearchResult(pid, which);
					// Next time, won't need to do this again.
					set(which, ret);
				}
			} catch(Exception e2) {
				getLogger().warn("Error getting result "
					+ which + " from " + o, e2);
			}
		}
		return(ret);
	}

}


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

}

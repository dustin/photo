// Copyright (c) 2000  Dustin Sallings <dustin@spy.net>
// arch-tag: 39370B0C-5D6D-11D9-A422-000A957659CC

package net.spy.photo.search;

import net.spy.photo.Cursor;
import net.spy.photo.PhotoDimensions;
import net.spy.photo.PhotoImageData;

/**
 * Represents results from a search.
 */
public class SearchResults extends Cursor {

	private PhotoDimensions maxSize=null;

	/**
	 * Get a search results object.
	 */
	public SearchResults() {
		super();
	}

	/**
	 * Get a search results object with an initial capacity.
	 */
	public SearchResults(int capacity) {
		super(capacity);
	}

	/**
	 * Add a search result to the list.
	 */
	public void add(PhotoImageData d) {
		super.add(new SearchResult(d, size()));
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

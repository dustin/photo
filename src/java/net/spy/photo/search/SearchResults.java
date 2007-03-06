// Copyright (c) 2000  Dustin Sallings <dustin@spy.net>

package net.spy.photo.search;

import net.spy.photo.Cursor;
import net.spy.photo.PhotoDimensions;
import net.spy.photo.PhotoImage;

/**
 * Represents results from a search.
 */
public class SearchResults extends Cursor<PhotoImage> {

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
	 * Set the maximum image size to be represented.
	 */
	public void setMaxSize(PhotoDimensions to) {
		this.maxSize=to;
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
	@Override
	public String toString() {
		return("Photo search results " + super.toString());
	}

}

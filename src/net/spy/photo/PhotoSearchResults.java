// Copyright (c) 2000  Dustin Sallings <dustin@spy.net>
// arch-tag: 39370B0C-5D6D-11D9-A422-000A957659CC

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

// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
// arch-tag: 592A439E-5D6D-11D9-B968-000A957659CC

package net.spy.photo.filter;

import net.spy.SpyObject;
import net.spy.photo.PhotoException;
import net.spy.photo.search.SearchResults;

/**
 * Superclass for search filters.
 */
public abstract class Filter extends SpyObject {

	/**
	 * Get an instance of Filter.
	 */
	public Filter() {
		super();
	}

	/**
	 * Filter a result set.
	 */
	public abstract SearchResults filter(SearchResults in)
		throws PhotoException;

}

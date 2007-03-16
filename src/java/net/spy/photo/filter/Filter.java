// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>

package net.spy.photo.filter;

import net.spy.SpyObject;
import net.spy.photo.PhotoException;
import net.spy.photo.search.SearchResults;

/**
 * Superclass for search filters.
 */
public abstract class Filter extends SpyObject {

	/**
	 * Filter a result set.
	 */
	public abstract SearchResults filter(SearchResults in)
		throws PhotoException;

}

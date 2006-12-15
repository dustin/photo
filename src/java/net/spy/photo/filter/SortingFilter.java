// Copyright (c) 2003  Dustin Sallings <dustin@spy.net>

package net.spy.photo.filter;

import net.spy.photo.PhotoException;
import net.spy.photo.search.SearchResults;

/**
 * Filter that affects sorting.
 */
public abstract class SortingFilter extends Filter {

	/**
	 * Sort directions.
	 */
	public static enum Sort { FORWARD, REVERSE }

	/**
	 * Get an instance of SortingFilter.
	 */
	public SortingFilter() {
		super();
	}

	/** 
	 * Filter the results and use the FORWARD sort direction.
	 */
	@Override
	public final SearchResults filter(SearchResults in) throws PhotoException {
		return(filter(in, Sort.FORWARD));
	}

	/**
	 * Filter a result set.
	 */
	public abstract SearchResults filter(SearchResults in, Sort direction)
		throws PhotoException;

}

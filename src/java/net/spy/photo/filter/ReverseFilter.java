// Copyright (c) 2004  Dustin Sallings <dustin@spy.net>
// arch-tag: 5E5256F3-5D6D-11D9-9287-000A957659CC

package net.spy.photo.filter;

import java.util.Collections;

import net.spy.photo.PhotoException;
import net.spy.photo.search.SearchResults;

/**
 * Reverse a search result.
 */
public class ReverseFilter extends Filter {

	/**
	 * Get an instance of ReverseFilter.
	 */
	public ReverseFilter() {
		super();
	}

	/** 
	 * Reverse the current order of the given search results.
	 */
	public SearchResults filter(SearchResults in) throws PhotoException {
		Collections.reverse(in.getAllObjects());
		// Seek back to the beginning
		in.setPageNumber(0);
		return(in);
	}

}

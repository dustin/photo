// Copyright (c) 2003  Dustin Sallings <dustin@spy.net>
//
// $Id: SortingFilter.java,v 1.1 2003/05/04 06:49:54 dustin Exp $

package net.spy.photo.filter;

import net.spy.photo.PhotoException;
import net.spy.photo.PhotoSearchResults;

/**
 * Filter that affects sorting.
 */
public abstract class SortingFilter extends Filter {

	/**
	 * Sort in chronological order.
	 */
	public static final int SORT_FORWARD=1;

	/**
	 * Sort in reverse chronological order.
	 */
	public static final int SORT_REVERSE=2;

	/**
	 * Get an instance of SortingFilter.
	 */
	public SortingFilter() {
		super();
	}

	/** 
	 * Filter the results and use the FORWARD sort direction.
	 */
	public final PhotoSearchResults filter(PhotoSearchResults in)
		throws PhotoException {

		return(filter(in, SORT_FORWARD));
	}

	/**
	 * Filter a result set.
	 */
	public abstract PhotoSearchResults filter(PhotoSearchResults in,
		int direction) throws PhotoException;

}

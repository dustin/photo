// Copyright (c) 2003  Dustin Sallings <dustin@spy.net>
//
// $Id: SortingFilter.java,v 1.1 2003/05/04 06:49:54 dustin Exp $

package net.spy.photo.filter;

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

	private int sortDirection=SORT_REVERSE;

	/**
	 * Get an instance of SortingFilter.
	 */
	public SortingFilter() {
		super();
	}

	/**
	 * Get the sort direction.
	 */
	public int getSortDirection() {
		return(sortDirection);
	}

	/**
	 * Set the sort direction.
	 */
	public void setSortDirection(int sortDirection) {
		this.sortDirection=sortDirection;
	}

}

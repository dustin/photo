// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: Filter.java,v 1.2 2002/07/10 03:38:08 dustin Exp $

package net.spy.photo.filter;

import net.spy.photo.PhotoException;
import net.spy.photo.PhotoSearchResults;

/**
 * Superclass for search filters.
 */
public abstract class Filter extends Object {

	/**
	 * Get an instance of Filter.
	 */
	public Filter() {
		super();
	}

	/**
	 * Filter a result set.
	 */
	public abstract PhotoSearchResults filter(PhotoSearchResults in)
		throws PhotoException;

}

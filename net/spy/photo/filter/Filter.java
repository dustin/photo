// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: Filter.java,v 1.3 2003/05/04 08:19:29 dustin Exp $

package net.spy.photo.filter;

import net.spy.SpyObject;

import net.spy.photo.PhotoException;
import net.spy.photo.PhotoSearchResults;

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
	public abstract PhotoSearchResults filter(PhotoSearchResults in)
		throws PhotoException;

}

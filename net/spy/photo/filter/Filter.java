// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: Filter.java,v 1.1 2002/06/29 07:13:56 dustin Exp $

package net.spy.photo.filter;

import net.spy.photo.*;

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

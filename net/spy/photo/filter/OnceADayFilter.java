// Copyright (c) 2003  Dustin Sallings <dustin@spy.net>
//
// $Id: OnceADayFilter.java,v 1.1 2003/05/04 06:49:54 dustin Exp $

package net.spy.photo.filter;

import java.util.Date;

/**
 * This filter randomly selects one picture per day from a group and throws
 * the rest away.
 */
public class OnceADayFilter extends DateFilter {

	/**
	 * Get an instance of OnceADayFilter.
	 */
	public OnceADayFilter() {
		super();
		setArrayGuess(4);
	}

	/** 
	 * Return the input date.
	 */
	protected Date roundDate(Date d) {
		return(d);
	}

}


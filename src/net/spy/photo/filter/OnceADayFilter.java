// Copyright (c) 2003  Dustin Sallings <dustin@spy.net>
//
// $Id: OnceADayFilter.java,v 1.2 2003/05/04 08:19:29 dustin Exp $

package net.spy.photo.filter;

import java.util.Date;
import java.util.Calendar;

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
		Calendar cal=Calendar.getInstance();
		cal.setTime(d);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.set(Calendar.HOUR, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		return(cal.getTime());
	}

}


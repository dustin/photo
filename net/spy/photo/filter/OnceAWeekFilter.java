// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: OnceAWeekFilter.java,v 1.1 2003/05/04 06:49:54 dustin Exp $

package net.spy.photo.filter;

import java.util.Calendar;
import java.util.Date;

/**
 * This filter randomly selects one picture per month from a group and
 * throws the rest away.
 */
public class OnceAWeekFilter extends DateFilter {

	/**
	 * Get an instance of OnceAWeekFilter.
	 */
	public OnceAWeekFilter() {
		super();
	}

	/** 
	 * Truncate the date to the beginning of the month.
	 */
	protected Date roundDate(Date d) {
		Calendar cal=Calendar.getInstance();
		cal.setTime(d);
		cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
		return(cal.getTime());
	}

}

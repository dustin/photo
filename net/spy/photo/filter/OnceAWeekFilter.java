// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: OnceAWeekFilter.java,v 1.2 2003/05/04 08:19:29 dustin Exp $

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
		cal.set(Calendar.HOUR, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		return(cal.getTime());
	}

}

// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
// arch-tag: 5BC8C568-5D6D-11D9-A13C-000A957659CC

package net.spy.photo.filter;

import java.util.Calendar;
import java.util.Date;

/**
 * This filter randomly selects one picture per month from a group and
 * throws the rest away.
 */
public class OnceAMonthFilter extends DateFilter {

	/**
	 * Get an instance of OnceAMonthFilter.
	 */
	public OnceAMonthFilter() {
		super();
	}

	/** 
	 * Truncate the date to the beginning of the month.
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

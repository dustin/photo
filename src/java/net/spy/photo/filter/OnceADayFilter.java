// Copyright (c) 2003  Dustin Sallings <dustin@spy.net>
// arch-tag: 5A32B07E-5D6D-11D9-817F-000A957659CC

package net.spy.photo.filter;

import java.util.Calendar;
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
	@Override
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


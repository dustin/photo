// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: OnceAMonthFilter.java,v 1.4 2002/07/04 00:19:26 dustin Exp $

package net.spy.photo.filter;

import java.util.*;
import java.text.*;

import net.spy.photo.*;

/**
 * This filter randomly selects one picture per month from a group and
 * throws the rest away.
 */
public class OnceAMonthFilter extends Filter {

	// Need this for date parsing.
	private SimpleDateFormat sdf=null;

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
	 * Get an instance of OnceAMonthFilter.
	 */
	public OnceAMonthFilter() {
		super();

		sdf=new SimpleDateFormat("yyyy-mm-DD");
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

	/**
	 * Filter the set.
	 */
	public PhotoSearchResults filter(PhotoSearchResults in)
		throws PhotoException {

		// Get a base copy of the result set.
		PhotoSearchResults rv=new PhotoSearchResults();
		rv.setMaxSize(in.getMaxSize());
		rv.setMaxRet(in.getMaxRet());

		// Do the actual processing

		// This is where the month groups go
		TreeMap months=new TreeMap();
		for(; in.hasMoreElements(); ) {
			PhotoImageData pid=(PhotoImageData)in.nextElement();

			// PhotoImageData currently has the dates as strings, this
			// sucks, but here we go.
			Calendar cal=Calendar.getInstance();
			try {
				cal.setTime(sdf.parse(pid.getTaken()));
			} catch(ParseException pe) {
				throw new PhotoException("Couldn't parse date", pe);
			}

			// Roll the date to the first of the month
			cal.set(Calendar.DAY_OF_MONTH,1);

			// Get the date back out, and we'll have a Comparable
			Date taken=cal.getTime();

			// Get a vector from the date
			ArrayList a=(ArrayList)months.get(taken);
			if(a==null) {
				a=new ArrayList();
				months.put(taken, a);
			}

			a.add(pid);
		}

		// OK, now get the parts (since that's a TreeMap, they'll be sorted)
        Collection values=months.values();
		// If we need to reverse the keys, do so now
		if(sortDirection==SORT_REVERSE) {
            // Make an ArrayList and reverse it, then have that be our
            // values
            ArrayList tmp=new ArrayList(values);
			Collections.reverse(tmp);
            values=tmp;
		}

		// Get a new random object
		Random r=new Random();

		// OK, now we'll flip through the keys to do one per month
		for(Iterator i=values.iterator(); i.hasNext(); ) {
			// Grab the vector
			List l=(List)i.next();

			// Get a random object from vector
			PhotoImageData pid=(PhotoImageData)l.get(r.nextInt(l.size()));

			// Add it to the results
			rv.add(pid);
		}

		// Return the new result set
		return(rv);
	}

}

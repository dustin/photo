// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
// arch-tag: 57D97AD6-5D6D-11D9-8AC6-000A957659CC

package net.spy.photo.filter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import net.spy.photo.PhotoException;
import net.spy.photo.PhotoImageData;
import net.spy.photo.search.SearchResults;

/**
 * This filter randomly selects pictures by date.
 */
public abstract class DateFilter extends SortingFilter {

	// Number of elements that there should be in an array by unit.
	private static final int DEFAULT_ARRAY_GUESS=16;
	private int arrayGuess=DEFAULT_ARRAY_GUESS;

	/**
	 * Get an instance of DateFilter.
	 */
	public DateFilter() {
		super();
	}

	/** 
	 * Set the array guess size to optimize the array allocation.  This
	 * should be set to the approximate number of items you expect there to
	 * be per time unit (i.e. the median number of pictures per month for
	 * the OnceAMonth filter, etc...)
	 * 
	 * @param to the array guess size
	 */
	protected void setArrayGuess(int to) {
		arrayGuess=to;
	}

	/** 
	 * Date truncation routine goes here.
	 * 
	 * @param d the input date
	 * @return the output date
	 */
	protected abstract Date roundDate(Date d);

	/**
	 * Filter the set.
	 */
	public SearchResults filter(SearchResults in, int direction)
		throws PhotoException {

		// Get a base copy of the result set.
		SearchResults rv=new SearchResults(in.size());
		rv.setMaxSize(in.getMaxSize());
		rv.setMaxRet(in.getMaxRet());

		// Do the actual processing

		// This is where the month groups go
		TreeMap<Date, Collection<PhotoImageData>> months = null;
		if(direction == SORT_REVERSE) {
			months = new TreeMap<Date, Collection<PhotoImageData>>(Collections
				.reverseOrder());
		} else {
			months = new TreeMap<Date, Collection<PhotoImageData>>();
		}
		for(; in.hasMoreElements();) {
			PhotoImageData pid=in.nextElement();

			Date dtmp=pid.getTaken();

			// Truncate the date appropriately
			Date taken=roundDate(dtmp);
			/*
			if(getLogger().isInfoEnabled()) {
				getLogger().info("Rounded " + sdf.format(dtmp)
					+ " to " + sdf.format(taken)
					+ " (from " + pid.getTaken() + ")");
			}
			*/

			// Get a vector from the date
			Collection<PhotoImageData> a=months.get(taken);
			if(a==null) {
				a=new ArrayList<PhotoImageData>(arrayGuess);
				months.put(taken, a);
			}

			a.add(pid);
		}

		// Get a new random object
		Random r=new Random();

		// OK, now we'll flip through the keys to do one per month
		for(Iterator i=months.entrySet().iterator(); i.hasNext(); ) {
			// The entry
			Map.Entry me=(Map.Entry)i.next();
			// Grab the List
			List l=(List)me.getValue();

			// Get a random object from vector
			PhotoImageData pid=(PhotoImageData)l.get(r.nextInt(l.size()));

			// Add it to the results
			rv.add(pid);

			// getLogger().info("Selected " + pid + " for " + me.getKey());

		}

		// Return the new result set
		return(rv);
	}

}

// Copyright (c) 2004  Dustin Sallings <dustin@spy.net>
// arch-tag: 50D73132-5D6D-11D9-9F28-000A957659CC

package net.spy.photo.search;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Observable;

import net.spy.SpyObject;
import net.spy.photo.Keyword;
import net.spy.photo.PhotoImageData;

/**
 * Search data indexer.
 */
public class SearchIndex extends Observable {

	/**
	 * Operator for ``and'' joins.
	 */
	public static final int OP_AND = 1;

	/**
	 * Operator for ``or'' joins.
	 */
	public static final int OP_OR = 2;

	private static SearchIndex instance = null;

	// Indexes
	private Map<Keyword, Set<PhotoImageData>> byKeyword = null;
	private Map<Integer, Set<PhotoImageData>> byCategory = null;
	private SortedMap<Date, Set<PhotoImageData>> byTaken = null;
	private SortedMap<Date, Set<PhotoImageData>> byTs = null;

	/**
	 * Get an instance of SearchIndex.
	 */
	private SearchIndex() {
		super();
	}

	/**
	 * Get the SearchIndex instance.
	 */
	public static synchronized SearchIndex getInstance() {
		if(instance == null) {
			instance = new SearchIndex();
		}
		return (instance);
	}

	@SuppressWarnings("unchecked")
	private void addOne(Map m, Object k, PhotoImageData pid) {
		Set s = (Set) m.get(k);
		if(s == null) {
			s = new HashSet<PhotoImageData>();
			m.put(k, s);
		}
		s.add(pid);
	}

	private void add(Map m, Collection c, PhotoImageData pid) {
		for(Iterator i = c.iterator(); i.hasNext();) {
			addOne(m, i.next(), pid);
		}
	}

	private void add(Map m, int i, PhotoImageData pid) {
		addOne(m, new Integer(i), pid);
	}

	private void add(Map m, Date d, PhotoImageData pid) {
		addOne(m, d, pid);
	}

	/**
	 * Update the indexes with a collection of PhotoImageData instances.
	 */
	public synchronized void update(Collection<PhotoImageData> vals) {
		byKeyword = new HashMap<Keyword, Set<PhotoImageData>>();
		byCategory = new HashMap<Integer, Set<PhotoImageData>>();
		byTaken = new TreeMap<Date, Set<PhotoImageData>>();
		byTs = new TreeMap<Date, Set<PhotoImageData>>();
		for(PhotoImageData pid : vals) {
			add(byKeyword, pid.getKeywords(), pid);
			add(byCategory, pid.getCatId(), pid);
			add(byTaken, pid.getTaken(), pid);
			add(byTs, pid.getTimestamp(), pid);
		}
		notifyObservers();
	}


	/**
	 * Get the set of images for the given category ID.
	 */
	public synchronized Set<PhotoImageData> getForCat(int i) {
		return (byCategory.get(i));
	}

	/**
	 * Get the set of images for the given collection of categories.
	 * 
	 * @param cats
	 *            a Collection of Integer objects.
	 */
	public synchronized Set<PhotoImageData>
		getForCats(Collection<Integer> cats) {
		return (getCombined(byCategory, cats, OP_OR));
	}

	/**
	 * Get the set of images for the given keyword.
	 */
	public synchronized Set<PhotoImageData> getForKeyword(Keyword k) {
		return (byKeyword.get(k));
	}

	private void checkOp(int op) {
		if(op == OP_AND) {
			// OK
		} else if(op == OP_OR) {
			// OK
		} else {
			throw new IllegalArgumentException("Invalid operator:  " + op);
		}
	}

	@SuppressWarnings("unchecked")
	private Set<PhotoImageData> getCombined(
		Map<? extends Object, Set<PhotoImageData>> m,
		Collection<? extends Object> c, int operator) {
		// Validate the operator
		checkOp(operator);

		Set<PhotoImageData> rv = new HashSet<PhotoImageData>();
		if(c.size() > 0) {
			// Get and add the first one
			Iterator i = c.iterator();
			Set<PhotoImageData> s = m.get(i.next());
			if(s != null) {
				rv.addAll(s);
			}
			// Now, deal with the rest of them
			for(; i.hasNext();) {
				Collection<PhotoImageData> stmp = m.get(i.next());
				if(stmp == null) {
					stmp = Collections.EMPTY_LIST;
				}
				if(operator == OP_AND) {
					rv.retainAll(stmp);
				} else {
					rv.addAll(stmp);
				}
			}
		}

		return (rv);
	}

	/**
	 * Get the set of images containing the given collection of keywords.
	 * 
	 * @param c
	 *            the collection
	 * @param operator
	 *            the join operator AND or OR
	 * @return the set
	 */
	public synchronized Set<PhotoImageData> getForKeywords(
		Collection<Keyword> c, int operator) {
		return (getCombined(byKeyword, c, operator));
	}

	private Set<PhotoImageData> getDateRangeSet(
		SortedMap<Date, Set<PhotoImageData>> sm, Date from, Date to) {
		SortedMap<Date, Set<PhotoImageData>> tail = sm;
		if(from != null) {
			tail = tail.tailMap(from);
		}
		SortedMap<Date, Set<PhotoImageData>> rvm = tail;
		if(to != null) {
			rvm = tail.headMap(to);
		}
		HashSet<PhotoImageData> rv = new HashSet<PhotoImageData>();
		for(Set<PhotoImageData> s : rvm.values()) {
			rv.addAll(s);
		}
		return (rv);
	}

	/**
	 * Get the set of images that were taken between given dates.
	 * 
	 * @param from
	 *            starting date (or null if there is no starting date).
	 * @param to
	 *            ending date (or null if there is no ending date).
	 * @return the Set of images.
	 */
	public synchronized Set<PhotoImageData> getForTaken(Date from, Date to) {
		return (getDateRangeSet(byTaken, from, to));
	}

	/**
	 * Get the set of images that were added between given dates.
	 * 
	 * @param from
	 *            starting date (or null if there is no starting date).
	 * @param to
	 *            ending date (or null if there is no ending date).
	 * @return the Set of images.
	 */
	public synchronized Set<PhotoImageData>
		getForTimestamp(Date from, Date to) {
		return (getDateRangeSet(byTs, from, to));
	}

}

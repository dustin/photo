// Copyright (c) 2004  Dustin Sallings <dustin@spy.net>

package net.spy.photo;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.Set;
import java.util.HashSet;
import java.util.TreeSet;
import java.util.Date;

import net.spy.SpyObject;

/**
 * Search data indexer.
 */
public class SearchIndex extends SpyObject {

	/** 
	 * Operator for ``and'' joins.
	 */
	public static final int OP_AND=1;
	/** 
	 * Operator for ``or'' joins.
	 */
	public static final int OP_OR=2;

	private static SearchIndex instance=null;

	// Indexes
	private Map byKeyword=null;
	private Map byCategory=null;
	private SortedMap byTaken=null;
	private SortedMap byTs=null;

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
		return(instance);
	}

	private void addOne(Map m, Object k, PhotoImageData pid) {
		Set s=(Set)m.get(k);
		if(s == null) {
			s=new HashSet();
			m.put(k, s);
		}
		s.add(pid);
	}

	private void add(Map m, Collection c, PhotoImageData pid) {
		for(Iterator i=c.iterator(); i.hasNext();) {
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
	public synchronized void update(Collection vals) {
		byKeyword=new HashMap();
		byCategory=new HashMap();
		byTaken=new TreeMap();
		byTs=new TreeMap();
		for(Iterator i=vals.iterator(); i.hasNext(); ) {
			PhotoImageData pid=(PhotoImageData)i.next();

			add(byKeyword, pid.getKeywords(), pid);
			add(byCategory, pid.getCatId(), pid);
			add(byTaken, pid.getTaken(), pid);
			add(byTs, pid.getTimestamp(), pid);
		}
		getLogger().info("Updated indices");
		/*
		getLogger().info("byKeyword:  " + byKeyword);
		getLogger().info("byCategory:  " + byCategory);
		getLogger().info("byTaken:  " + byTaken);
		getLogger().info("byTs:  " + byTs);
		*/
	}

	/** 
	 * Get the set of images for the given category ID.
	 */
	public synchronized Set getForCat(int i) {
		Set rv=(Set)byCategory.get(new Integer(i));
		return(rv);
	}

	/** 
	 * Get the set of images for the given collection of categories.
	 * 
	 * @param cats a Collection of Integer objects.
	 * @param operator the operator
	 */
	public synchronized Set getForCats(Collection cats) {
		return(getCombined(byCategory, cats, OP_OR));
	}

	/** 
	 * Get the set of images for the given keyword.
	 */
	public synchronized Set getForKeyword(Keyword k) {
		Set rv=(Set)byKeyword.get(k);
		return(rv);
	}

	private void checkOp(int operator) {
		if(operator == OP_AND) {
			// OK
		} else if(operator == OP_OR) {
			// OK
		} else {
			throw new IllegalArgumentException("Invalid operator:  "+operator);
		}
	}

	private String opToString(int op) {
		String rv="INVALID";
		if(op == OP_AND) {
			rv="AND";
		} else if(op == OP_OR) {
			rv="OR";
		}
		return(rv);
	}

	private Set getCombined(Map m, Collection c, int operator) {
		// Validate the operator
		checkOp(operator);

		Set rv=new HashSet();
		if(c.size() > 0) {
			// Get and add the first one
			Iterator i=c.iterator();
			Set s=(Set)m.get(i.next());
			if(s != null) {
				rv.addAll(s);
			}
			// Now, deal with the rest of them
			for(; i.hasNext(); ) {
				Collection stmp=(Collection)m.get(i.next());
				if(stmp == null) {
					stmp=Collections.EMPTY_LIST;
				}
				if(operator == OP_AND) {
					rv.retainAll(stmp);
				} else {
					rv.addAll(stmp);
				}
			}
		}

		return(rv);
	}

	/** 
	 * Get the set of images containing the given collection of keywords.
	 * 
	 * @param c the collection
	 * @param operator the join operator AND or OR
	 * @return the set
	 */
	public synchronized Set getForKeywords(Collection c, int operator) {
		return(getCombined(byKeyword, c, operator));
	}

	private Set getDateRangeSet(SortedMap sm, Date from, Date to) {
		SortedMap tail=sm;
		if(from != null) {
			tail=tail.tailMap(from);
		}
		SortedMap rvm=tail;
		if(to != null) {
			rvm=tail.headMap(to);
		}
		return(new HashSet(rvm.values()));
	}

	/** 
	 * Get the set of images that were taken between given dates.
	 * 
	 * @param from starting date (or null if there is no starting date).
	 * @param to ending date (or null if there is no ending date).
	 * @return the Set of images.
	 */
	public synchronized Set getForTaken(Date from, Date to) {
		return(getDateRangeSet(byTaken, from, to));
	}

	/** 
	 * Get the set of images that were added between given dates.
	 * 
	 * @param from starting date (or null if there is no starting date).
	 * @param to ending date (or null if there is no ending date).
	 * @return the Set of images.
	 */
	public synchronized Set getForTimestamp(Date from, Date to) {
		return(getDateRangeSet(byTs, from, to));
	}

}

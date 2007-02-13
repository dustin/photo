// Copyright (c) 2004  Dustin Sallings <dustin@spy.net>

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
import java.util.concurrent.atomic.AtomicReference;

import net.spy.SpyObject;
import net.spy.photo.Keyword;
import net.spy.photo.PhotoImageData;

/**
 * Search data indexer.
 */
@SuppressWarnings("unchecked")
public class SearchIndex extends SpyObject {

	/**
	 * Operators for join operations.
	 */
	public static enum OP { AND, OR }

	private static AtomicReference<SearchIndex> instanceRef = 
		new AtomicReference<SearchIndex>(null);

	// Indexes
	private Map<Keyword, Set<PhotoImageData>> byKeyword = null;
	private Map<Integer, Set<PhotoImageData>> byCategory = null;
	// These are actually by date.
	private SortedMap<Integer, Set<PhotoImageData>> byTaken = null;
	private SortedMap<Integer, Set<PhotoImageData>> byTs = null;

	// All images that can be classified as a variant of another image.
	private Set<PhotoImageData> variants=null;

	/**
	 * Get an instance of SearchIndex.
	 */
	private SearchIndex(Collection<PhotoImageData> vals) {
		super();
		byKeyword = new HashMap<Keyword, Set<PhotoImageData>>();
		byCategory = new HashMap<Integer, Set<PhotoImageData>>();
		byTaken = new TreeMap<Integer, Set<PhotoImageData>>();
		byTs = new TreeMap<Integer, Set<PhotoImageData>>();	
		variants = new HashSet<PhotoImageData>();
		long start=System.currentTimeMillis();
		for(PhotoImageData pid : vals) {
			add(byKeyword, pid.getKeywords(), pid);
			add(byCategory, pid.getCatId(), pid);
			add(byTaken, pid.getTaken(), pid);
			add(byTs, pid.getTimestamp(), pid);
			variants.addAll(pid.getVariants());
		}
		immutifyIndex(byKeyword);
		immutifyIndex(byCategory);
		immutifyIndex(byTaken);
		immutifyIndex(byTs);
		variants=new ImmutableArraySet<PhotoImageData>(variants);
		getLogger().info("Indexed %d images (%d are variants) in %sms",
				vals.size(), variants.size(),
				(System.currentTimeMillis() - start));
	}

	private void immutifyIndex(Map<?, Set<PhotoImageData>> m) {
		for(Map.Entry<?, Set<PhotoImageData>> me : m.entrySet()) {
			me.setValue(new ImmutableArraySet<PhotoImageData>(me.getValue()));
		}
	}

	/**
	 * Update the indexes with a collection of PhotoImageData instances.
	 */
	public static void update(Collection<PhotoImageData> vals) {
		instanceRef.set(new SearchIndex(vals));
		SearchCache.getInstance().clear();
	}

	/**
	 * Get the SearchIndex instance.
	 */
	public static SearchIndex getInstance() {
		SearchIndex rv=instanceRef.get();
		assert rv != null;
		return rv;
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

	private int dateToInt(Date d) {
		// Convert the millisecond time to days.
		int rv=(int) (d.getTime() / 86400000);
		return rv;
	}

	private void add(Map m, Date d, PhotoImageData pid) {
		addOne(m, new Integer(dateToInt(d)), pid);
	}

	/**
	 * Get the set of images for the given category ID.
	 */
	public Set<PhotoImageData> getForCat(int i) {
		return (byCategory.get(i));
	}

	/**
	 * Get the set of images for the given collection of categories.
	 * 
	 * @param cats
	 *            a Collection of Integer objects.
	 */
	@SuppressWarnings("unchecked")
	public Set<PhotoImageData> getForCats(Collection<Integer> cats) {
		StringBuilder cacheKey=new StringBuilder();
		cacheKey.append("orcats ");
		for(Integer cat : cats) {
			cacheKey.append(cat).append(",");
		}
		SearchCache sc=SearchCache.getInstance();
		Set<PhotoImageData> cachedStuff=
			(Set<PhotoImageData>)sc.get(cacheKey.toString());
		if(cachedStuff == null) {
			cachedStuff=getCombined(byCategory, cats, OP.OR);
			sc.store(cacheKey.toString(), cachedStuff);
		}
		return cachedStuff;
	}

	/**
	 * Get the set of images for the given keyword.
	 */
	public Set<PhotoImageData> getForKeyword(Keyword k) {
		return (byKeyword.get(k));
	}

	private Set<PhotoImageData> getCombined(
		Map<? extends Object, Set<PhotoImageData>> m,
		Collection<? extends Object> c, OP operator) {

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
					stmp = Collections.emptyList();
				}
				if(operator == OP.AND) {
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
	public Set<PhotoImageData> getForKeywords(
		Collection<Keyword> c, OP operator) {
		return (getCombined(byKeyword, c, operator));
	}

	private Set<PhotoImageData> getDateRangeSet(
		SortedMap<Integer, Set<PhotoImageData>> sm, Date fromD, Date toD) {
		Integer from=dateToInt(fromD);
		Integer to=dateToInt(toD);
		SortedMap<Integer, Set<PhotoImageData>> tail = sm;
		if(from != null) {
			tail = tail.tailMap(from);
		}
		SortedMap<Integer, Set<PhotoImageData>> rvm = tail;
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
	public Set<PhotoImageData> getForTaken(Date from, Date to) {
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
	public Set<PhotoImageData> getForTimestamp(Date from, Date to) {
		return (getDateRangeSet(byTs, from, to));
	}

}

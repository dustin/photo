// Copyright (c) 1999  Dustin Sallings <dustin@spy.net>

package net.spy.photo.search;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicReference;

import javax.servlet.ServletException;

import net.spy.SpyObject;
import net.spy.cache.SimpleCache;
import net.spy.photo.Category;
import net.spy.photo.CategoryFactory;
import net.spy.photo.Keyword;
import net.spy.photo.KeywordFactory;
import net.spy.photo.PhotoConfig;
import net.spy.photo.PhotoDimensions;
import net.spy.photo.PhotoImageData;
import net.spy.photo.PhotoUtil;
import net.spy.photo.User;
import net.spy.photo.sp.DeleteSearch;
import net.spy.photo.sp.InsertSearch;
import net.spy.photo.struts.SearchForm;
import net.spy.stat.Stats;
import net.spy.util.CloseUtil;


/**
 * Perform searches.
 */
public class Search extends SpyObject {

	private static final String CHARSET = "UTF-8";
	private static AtomicReference<Search> instanceRef = 
		new AtomicReference<Search>(null);

	private static enum CacheType { RESULTS, KEYWORDS }
	private static enum SortOrder { BY_TS, BY_TAKEN }

	/**
	 * Get a Search instance.
	 */
	protected Search() {
		super();
	}

	/**
	 * Get an instance of search.
	 */
	public static Search getInstance() {
		Search rv=instanceRef.get();
		if(rv == null) {
			rv = new Search();
			instanceRef.set(rv);
		}
		return (rv);
	}

	/**
	 * Save a search.
	 */
	public void saveSearch(String name, String search, User user)
		throws Exception {
		if(user == null || name == null || search == null) {
			throw new Exception("Weird, invalid stuff.");
		}

		if(!user.canAdd()) {
			throw new Exception("No permission to save searches.");
		}

		InsertSearch is=new InsertSearch(PhotoConfig.getInstance());
		try {
			is.setName(name);
			is.setAddedBy(user.getId());
			is.setSearchData(search);
			is.setTimestamp(new java.sql.Timestamp(System.currentTimeMillis()));

			int affected=is.executeUpdate();
			assert affected == 1 : "Expected to affect 1 record, affected "
				+ affected;

			// Clear the saved search cache so stuff shows up immediately
			SimpleCache.getInstance().remove(SavedSearch.CACHE_KEY);
		} catch(Exception e) {
			getLogger().error("Error saving search", e);
		} finally {
			CloseUtil.close(is);
		}
	}

	/**
	 * Delete the search with the given ID.
	 * 
	 * @param searchId the ID of the search to delete
	 */
	public void deleteSearch(int searchId, User user) throws Exception {
		if(!user.isInRole(User.ADMIN)) {
			throw new Exception("User is not an admin.");
		}
		DeleteSearch ds=new DeleteSearch(PhotoConfig.getInstance());
		try {
			ds.setSearchId(searchId);
			int affected=ds.executeUpdate();
			assert affected == 1 : "Expected to delete 1 row, deleted "
				+ affected;
			// Clear the saved search cache so stuff shows up immediately
			SimpleCache.getInstance().remove(SavedSearch.CACHE_KEY);
		} finally {
			CloseUtil.close(ds);
		}
	}

	// URLEncode using CHARSET
	private String urlEncode(String msg) {
		String rv = null;
		try {
			rv = URLEncoder.encode(msg, CHARSET);
		} catch(java.io.UnsupportedEncodingException e) {
			throw new RuntimeException(CHARSET + " is not supported.", e);
		}
		return (rv);
	}

	private void addParam(StringBuilder sb, String name, String val) {
		if(val != null) {
			sb.append(name);
			sb.append('=');
			sb.append(urlEncode(val));
			sb.append('&');
		}
	}

	/**
	 * Encode the search from a SearchForm.
	 */
	public String encodeSearch(SearchForm form) {
		StringBuilder sb = new StringBuilder(512);

		addParam(sb, "field", form.getField());
		addParam(sb, "keyjoin", form.getKeyjoin());
		addParam(sb, "what", form.getWhat());
		addParam(sb, "tstart", form.getTstart());
		addParam(sb, "tend", form.getTend());
		addParam(sb, "start", form.getStart());
		addParam(sb, "end", form.getEnd());
		addParam(sb, "order", form.getOrder());
		addParam(sb, "sdirection", form.getSdirection());
		addParam(sb, "maxret", form.getMaxret());
		addParam(sb, "filter", form.getFilter());
		addParam(sb, "action", form.getAction());

		String cats[]=form.getCat();
		if(cats != null) {
			for(String cat : cats) {
				addParam(sb, "cat", cat);
			}
		}

		return(sb.toString());
	}

	/**
	 * Perform a search.
	 * 
	 * @param form the search form
	 * @param user the user requesting the search
	 * @param dims the maximum dimensions for any given entry
	 */
	public SearchResults performSearch(
		SearchForm form, User user, PhotoDimensions dims) throws Exception {
		long start=System.currentTimeMillis();
		SearchCache sc=SearchCache.getInstance();
		CacheKey ck=new CacheKey(CacheType.RESULTS, user, dims, form);
		SearchResults rv=(SearchResults)sc.get(ck);
		boolean cached=true;
		if(rv == null) {
			cached=false;
			rv=realPerformSearch(form, user, dims);
			sc.store(ck, rv.clone());
		} else {
			rv=(SearchResults)rv.clone();
		}

		long end=System.currentTimeMillis();
		getLogger().info("Completed search%s for %s in %dms",
				(cached ? " (cached)" : ""), user, (end - start));
		Stats.getComputingStat(cached?"search.cached":"search").add(end-start);
		return(rv);
	}

	private SearchResults realPerformSearch(SearchForm form, User user,
			PhotoDimensions dims) throws Exception {

		// Get the search index
		SearchIndex index = SearchIndex.getInstance();

		getLogger().debug("Performing search.");

		// Figure out how the thing should be sorted.
		Comparator<PhotoImageData> comp = null;
		if("a.ts".equals(form.getOrder())) {
			comp = new TimestampComparator();
		} else {
			comp = new TakenComparator();
		}
		if("desc".equals(form.getSdirection())) {
			comp = new ReverseComparator<PhotoImageData>(comp);
		}

		// This is the result set of images we want to display and maintain
		// their sort order
		Set<PhotoImageData> rset = new TreeSet<PhotoImageData>(comp);

		// Handle any category first
		String atmp[] = form.getCat();
		if(atmp != null && atmp.length > 0) {
			ArrayList<Integer> al = new ArrayList<Integer>(atmp.length);
			for(String s : atmp) {
				al.add(new Integer(s));
			}
			// Get rid of anything that's not valid for the current user
			al.retainAll(getValidCats(user));
			getLogger().debug("Retaining images with cats %s", al);
			rset.addAll(index.getForCats(al));
		} else {
			getLogger().debug("Getting images for all categories");
			rset.addAll(index.getForCats(getValidCats(user)));
		}
		getLogger().debug("Starting with %d images", rset.size());

		// Check to see if there's a field entry
		if("keywords".equals(form.getField())) {
			processKeywords(rset, form.getWhat(), form.getKeyjoin());
		} else if("descr".equals(form.getField())) {
			processInfo(rset, form.getWhat(), form.getKeyjoin());
		} else {
			String stmp = form.getWhat();
			if(stmp != null && stmp.length() > 0) {
				throw new ServletException("Invalid field:  " + form.getField());
			}
		}
		getLogger().debug("After keywords:  %d images", rset.size());

		// Check for any of the date entries
		processDates(rset, form.getStart(), form.getEnd(),
				form.getTstart(), form.getTend());
		getLogger().debug("After dates:  %d images", rset.size());

		// Populate the results
		SearchResults results = new SearchResults();
		results.setMaxSize(dims);
		for(PhotoImageData r : rset) {
			results.add(r);
		}

		return (results);
	}

	/**
	 * Get all keywords applicable to the given user within the given search.
	 * 
	 * @param u the given user
	 * @param sf the search form for finding images to extract keywords
	 * @return all of the keywords available to the given user
	 */
	public Collection<KeywordMatch> getKeywordsForUser(User u, SearchForm sf)
		throws Exception {
		SearchCache sc=SearchCache.getInstance();
		CacheKey ck=new CacheKey(CacheType.KEYWORDS, u, null, sf);
		@SuppressWarnings("unchecked")
		Collection<KeywordMatch> rv=(Collection<KeywordMatch>)sc.get(ck);
		if(rv == null) {
			rv=realGetKeywordsForUser(u, sf);
			sc.store(ck, rv);
		}
		return(rv);
	}

	private Collection<KeywordMatch> realGetKeywordsForUser(User u,
			SearchForm sf) throws Exception {
		Map<String, KeywordMatch> rv=new TreeMap<String, KeywordMatch>();
		ParallelSearch ps=ParallelSearch.getInstance();
		for(PhotoImageData pid : ps.performSearch(sf, u).getAllObjects()) {
			for(Keyword kw : pid.getKeywords()) {
				KeywordMatch km=rv.get(kw.getKeyword());
				if(km == null) {
					km=new KeywordMatch(kw, pid.getId());
					rv.put(kw.getKeyword(), km);
				}
				km.increment();
			}
		}
		return(Collections.unmodifiableCollection(rv.values()));
	}

	/**
	 * Get all of the keywords applicable to the given user.
	 * 
	 * @param u the given user
	 * @return a map of 
	 * @throws Exception
	 */
	public Collection<KeywordMatch> getKeywordsForUser(User u)
		throws Exception {
		SearchForm sf=new SearchForm();
		sf.setSdirection("desc");
		return(getKeywordsForUser(u, sf));
	}

	// In the case of dates, we do an explicit OR between the two date range
	// sets, and then AND the results back to current response set
	private void processDates(
		Set<PhotoImageData> rset, String astart, String aend, String tstart,
		String tend) throws Exception {
		Collection<PhotoImageData> aset = processRange(SortOrder.BY_TS,
				astart, aend);
		Collection<PhotoImageData> tset = processRange(SortOrder.BY_TAKEN,
				tstart, tend);

		// Only process dates if one of them was not null
		if(aset != null || tset != null) {
			Set<PhotoImageData> combined = new HashSet<PhotoImageData>();
			if(aset != null) {
				getLogger().debug("Got set by added date: %d ", aset.size());
				combined.addAll(aset);
			}
			if(tset != null) {
				getLogger().debug("Got set by taken date:  %d", tset.size());
				combined.addAll(tset);
			}

			getLogger().debug("Total images by date:  %d", combined.size());
			rset.retainAll(combined);
		}
	}

	private void processKeywords(
		Set<PhotoImageData> rset, String kw, String keyjoin) throws Exception {

		if(kw == null) {
			kw = "";
		}
		// Figure out the operation
		SearchIndex.OP joinop = SearchIndex.OP.AND;
		if("or".equals(keyjoin)) {
			joinop = SearchIndex.OP.OR;
		}
		// Flip through the keywords
		boolean missingKw = false;
		KeywordFactory kf=KeywordFactory.getInstance();
		KeywordFactory.Keywords kws=kf.getKeywords(kw, false);
		if(kws.getMissing().size() > 0) {
			getLogger().debug("Unknown keywords:  %s", kws.getMissing());
			missingKw=true;
		}

		SearchIndex index = SearchIndex.getInstance();

		if(joinop == SearchIndex.OP.AND && missingKw) {
			// If the joinop is AND and an invalid keyword was requested, just
			// clear the set, we're done
			getLogger().debug("ANDing an unknown keyword");
			rset.clear();
		} else if(kws.getPositive().size() > 0) {
			// Remove everything that doesn't match our keywords (unless we
			// don't have any)
			getLogger().debug("Got images with keywords %s", kws.getPositive());
			Set<PhotoImageData> keyset =
				index.getForKeywords(kws.getPositive(), joinop);
			rset.retainAll(keyset);
		}
		if(kws.getNegative().size() > 0) {
			// Remove any images that match our ``anti-keywords'' by performing
			// a relative complement of the current result set the union of all
			// of the images matching the anti-keywords
			getLogger().debug("Removing images with keywords %s",
					kws.getNegative());
			Set<PhotoImageData> keyset =
				index.getForKeywords(kws.getNegative(), SearchIndex.OP.OR);
			rset.removeAll(keyset);
		}
	}

	private void processInfo(Set<PhotoImageData> rset, String kw,
		String keyjoin) {
		// Find all of the words
		ArrayList<String> words = new ArrayList<String>();
		StringTokenizer st = new StringTokenizer(kw.toLowerCase());
		while(st.hasMoreTokens()) {
			words.add(st.nextToken());
		}
		// Figure out the operation
		SearchIndex.OP joinop = SearchIndex.OP.AND;
		if("or".equals(keyjoin)) {
			joinop = SearchIndex.OP.OR;
		}

		for(Iterator<PhotoImageData> ri = rset.iterator(); ri.hasNext();) {
			PhotoImageData pid = ri.next();
			String info = pid.getDescr().toLowerCase();
			boolean matchedone = false;
			boolean matchedall = true;
			for(String word : words) {
				if(info.indexOf(word) >= 0) {
					matchedone = true;
				} else {
					matchedall = false;
				}
			}
			if(matchedone) {
				if(joinop == SearchIndex.OP.AND && !matchedall) {
					// And requires all to match, but we didn't match all
					ri.remove();
				}
			} else {
				// Didn't match anything
				ri.remove();
			}
		}
	}

	private Collection<PhotoImageData> processRange(
		SortOrder which, String start, String end) throws Exception {

		SearchIndex index = SearchIndex.getInstance();
		Date s = PhotoUtil.parseDate(start);
		Date e = PhotoUtil.parseDate(end);

		Collection<PhotoImageData> matches = null;

		if(s != null || e != null) {
			switch(which) {
				case BY_TS:
					matches = index.getForTimestamp(s, e);
					break;
				case BY_TAKEN:
					matches = index.getForTaken(s, e);
					break;
				default:
					throw new IllegalArgumentException("Invalid which:  "
						+ which);
			}
		}
		return (matches);
	}

	private Collection<Integer> getValidCats(User u) throws Exception {
		// Flip through all of the categories and get them as Integers
		Collection<Integer> validCats = new ArrayList<Integer>(16);
		CategoryFactory cf = CategoryFactory.getInstance();
		for(Category cat :
			cf.getCatList(u.getId(), CategoryFactory.ACCESS_READ)) {
			validCats.add(cat.getId());
		}
		return (validCats);
	}

	static abstract class PIDComparator
		implements Comparator<PhotoImageData> {

		@Override
		public boolean equals(Object ob) {
			return (ob.getClass() == getClass());
		}

		protected abstract int doCompare(
			PhotoImageData pid1, PhotoImageData pid2);

		public int compare(PhotoImageData pid1, PhotoImageData pid2) {
			int rv = doCompare(pid1, pid2);
			if(rv == 0) {
				rv = (pid1.getId() - pid2.getId());
			}
			return (rv);
		}

	}

	static class TimestampComparator extends PIDComparator {
		@Override
		public int doCompare(PhotoImageData pid1, PhotoImageData pid2) {
			return (pid1.getTimestamp().compareTo(pid2.getTimestamp()));
		}
	}

	static class TakenComparator extends PIDComparator {
		@Override
		public int doCompare(PhotoImageData pid1, PhotoImageData pid2) {
			return (pid1.getTaken().compareTo(pid2.getTaken()));
		}
	}

	private static class ReverseComparator<T>
		implements Comparator<T> {
		private Comparator<T> comp = null;

		public ReverseComparator(Comparator<T> c) {
			super();
			this.comp = c;
		}

		@Override
		public boolean equals(Object ob) {
			return (ob.getClass() == getClass());
		}

		public int compare(T ob1, T ob2) {
			return (0 - comp.compare(ob1, ob2));
		}
	}

	private static class CacheKey {
		private CacheType type=null;
		private int uid=0;
		private String dims=null;
		private SearchForm form=null;

		public CacheKey(CacheType t, User u, PhotoDimensions d, SearchForm f) {
			super();
			type=t;
			uid=u.getId();
			if(d == null) {
				dims="";
			} else {
				dims=d.getWidth() + "x" + d.getHeight();
			}
			form=f;
		}

		@Override
		public boolean equals(Object o) {
			boolean rv=false;
			if(o instanceof CacheKey) {
				CacheKey ck=(CacheKey)o;
				rv=(type == ck.type && uid == ck.uid && dims.equals(ck.dims)
						&& form.equals(ck.form));
			}
			return(rv);
		}

		@Override
		public int hashCode() {
			return(type.hashCode() ^ uid ^ dims.hashCode() ^ form.hashCode());
		}
		
	}
}

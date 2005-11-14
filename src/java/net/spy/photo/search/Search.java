// Copyright (c) 1999  Dustin Sallings <dustin@spy.net>
// arch-tag: 34B48890-5D6D-11D9-A728-000A957659CC

package net.spy.photo.search;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

import javax.servlet.ServletException;

import net.spy.SpyObject;
import net.spy.photo.Category;
import net.spy.photo.CategoryFactory;
import net.spy.photo.Keyword;
import net.spy.photo.KeywordFactory;
import net.spy.photo.PhotoConfig;
import net.spy.photo.PhotoImageData;
import net.spy.photo.PhotoSessionData;
import net.spy.photo.PhotoUtil;
import net.spy.photo.User;
import net.spy.photo.sp.InsertSearch;
import net.spy.photo.struts.SearchForm;
import net.spy.util.Base64;

/**
 * Perform searches.
 */
public class Search extends SpyObject {

	private static final String CHARSET = "UTF-8";

	private static final int BY_TS = 1;

	private static final int BY_TAKEN = 2;

	private static Search instance = null;

	/**
	 * Get a Search instance.
	 */
	private Search() {
		super();
	}

	/**
	 * Get an instance of search.
	 */
	public static synchronized Search getInstance() {
		if(instance == null) {
			instance = new Search();
		}
		return (instance);
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

		try {
			InsertSearch is = new InsertSearch(PhotoConfig.getInstance());
			is.setName(name);
			is.setAddedBy(user.getId());
			is.setSearchData(search);
			is.setTimestamp(new java.sql.Timestamp(System.currentTimeMillis()));

			is.executeUpdate();

			is.close();
		} catch(Exception e) {
			getLogger().error("Error saving search", e);
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

	/**
	 * Encode the search from a SearchForm.
	 */
	public String encodeSearch(SearchForm form) {
		StringBuffer sb = new StringBuffer(512);

		if(form.getField() != null) {
			sb.append("field");
			sb.append('=');
			sb.append(urlEncode(form.getField()));
			sb.append('&');
		}
		if(form.getKeyjoin() != null) {
			sb.append("keyjoin");
			sb.append('=');
			sb.append(urlEncode(form.getKeyjoin()));
			sb.append('&');
		}
		if(form.getWhat() != null) {
			sb.append("what");
			sb.append('=');
			sb.append(urlEncode(form.getWhat()));
			sb.append('&');
		}
		if(form.getTstart() != null) {
			sb.append("tstart");
			sb.append('=');
			sb.append(urlEncode(form.getTstart()));
			sb.append('&');
		}
		if(form.getTend() != null) {
			sb.append("tend");
			sb.append('=');
			sb.append(urlEncode(form.getTend()));
			sb.append('&');
		}
		if(form.getStart() != null) {
			sb.append("start");
			sb.append('=');
			sb.append(urlEncode(form.getStart()));
			sb.append('&');
		}
		if(form.getEnd() != null) {
			sb.append("end");
			sb.append('=');
			sb.append(urlEncode(form.getEnd()));
			sb.append('&');
		}
		if(form.getOrder() != null) {
			sb.append("order");
			sb.append('=');
			sb.append(urlEncode(form.getOrder()));
			sb.append('&');
		}
		if(form.getSdirection() != null) {
			sb.append("sdirection");
			sb.append('=');
			sb.append(urlEncode(form.getSdirection()));
			sb.append('&');
		}
		if(form.getMaxret() != null) {
			sb.append("maxret");
			sb.append('=');
			sb.append(urlEncode(form.getMaxret()));
			sb.append('&');
		}
		if(form.getFilter() != null) {
			sb.append("filter");
			sb.append('=');
			sb.append(urlEncode(form.getFilter()));
			sb.append('&');
		}
		if(form.getAction() != null) {
			sb.append("action");
			sb.append('=');
			sb.append(urlEncode(form.getAction()));
			sb.append('&');
		}

		if(form.getCat() != null) {
			String cats[] = form.getCat();
			for(int i = 0; i < cats.length; i++) {
				sb.append("cat");
				sb.append('=');
				sb.append(urlEncode(cats[i]));
				sb.append('&');
			}
		}

		Base64 base64 = new Base64();
		String out = base64.encode(sb.toString().getBytes());
		return (out);
	}

	public SearchResults performSearch(
		SearchForm form, PhotoSessionData sessionData) throws Exception {

		// Get the search index
		SearchIndex index = SearchIndex.getInstance();

		getLogger().debug("Performing search.");

		// Figure out how the thing should be sorted.
		Comparator<PhotoImageData> comp = null;
		if("a.ts".equals(form.getOrder())) {
			comp = new TimestampComparator();
		} else {
			comp = new TakenComprator();
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
			for(int i = 0; i < atmp.length; i++) {
				al.add(new Integer(atmp[i]));
			}
			// Get rid of anything that's not valid for the current user
			al.retainAll(getValidCats(sessionData.getUser()));
			if(getLogger().isDebugEnabled()) {
				getLogger().debug("Retaining images with cats " + al);
			}
			rset.addAll(index.getForCats(al));
		} else {
			if(getLogger().isDebugEnabled()) {
				getLogger().debug("Getting images for all categories");
			}
			rset.addAll(index.getForCats(getValidCats(sessionData.getUser())));
		}
		if(getLogger().isDebugEnabled()) {
			getLogger().debug("Starting with " + rset.size() + " images");
		}

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
		if(getLogger().isDebugEnabled()) {
			getLogger().debug("After keywords:  " + rset.size() + " images");
		}

		// Check for any of the date entries
		processDates(
			rset, form.getStart(), form.getEnd(), form.getTstart(), form
				.getTend());
		if(getLogger().isDebugEnabled()) {
			getLogger().debug("After dates:  " + rset.size() + " images");
		}

		// Populate the results
		SearchResults results = new SearchResults();
		results.setMaxSize(sessionData.getOptimalDimensions());
		int resultId = 0;
		for(Iterator i = rset.iterator(); i.hasNext();) {
			PhotoImageData r = (PhotoImageData)i.next();
			results.add(new SearchResult(r, resultId));
			resultId++;
		}

		return (results);
	}

	// In the case of dates, we do an explicit OR between the two date range
	// sets, and then AND the results back to current response set
	private void processDates(
		Set<PhotoImageData> rset, String astart, String aend, String tstart,
		String tend) throws Exception {
		Collection<PhotoImageData> aset = processRange(BY_TS, astart, aend);
		Collection<PhotoImageData> tset = processRange(BY_TAKEN, tstart, tend);

		// Only process dates if one of them was not null
		if(aset != null || tset != null) {
			Set<PhotoImageData> combined = new HashSet<PhotoImageData>();
			if(aset != null) {
				getLogger().debug("Got set by added date:  " + aset.size());
				combined.addAll(aset);
			}
			if(tset != null) {
				getLogger().debug("Got set by taken date:  " + tset.size());
				combined.addAll(tset);
			}

			if(getLogger().isDebugEnabled()) {
				getLogger().debug("Total images by date:  " + combined.size());
			}
			rset.retainAll(combined);
		}
	}

	private void processKeywords(
		Set<PhotoImageData> rset, String kw, String keyjoin) throws Exception {

		// Lookup the keywords
		ArrayList<Keyword> keywords = new ArrayList<Keyword>();
		if(kw == null) {
			kw = "";
		}
		// Figure out the operation
		int joinop = SearchIndex.OP_AND;
		if("or".equals(keyjoin)) {
			joinop = SearchIndex.OP_OR;
		}
		// Flip through the keywords
		boolean missingKw = false;
		KeywordFactory kf=KeywordFactory.getInstance();
		StringTokenizer st = new StringTokenizer(kw);
		while(st.hasMoreTokens()) {
			String kwstring = st.nextToken();
			Keyword k = kf.getKeyword(kwstring);
			if(k != null) {
				keywords.add(k);
			} else {
				getLogger().debug("Unknown keyword:  " + kwstring);
				missingKw = true;
			}
		}
		if(joinop == SearchIndex.OP_AND && missingKw) {
			// If the joinop is AND and an invalid keyword was requested, just
			// clear the set, we're done
			getLogger().debug("ANDing an unknown keyword");
			rset.clear();
		} else if(keywords.size() > 0) {
			// Remove everything that doesn't match our keywords (unless we
			// don't have any)
			if(getLogger().isDebugEnabled()) {
				getLogger().debug("Got images with keywords " + keywords);
			}
			SearchIndex index = SearchIndex.getInstance();
			Set keyset = index.getForKeywords(keywords, joinop);
			rset.retainAll(keyset);
		}
	}

	private void processInfo(Set<PhotoImageData> rset, String kw, String keyjoin) {
		// Find all of the words
		ArrayList<String> words = new ArrayList<String>();
		StringTokenizer st = new StringTokenizer(kw.toLowerCase());
		while(st.hasMoreTokens()) {
			words.add(st.nextToken());
		}
		// Figure out the operation
		int joinop = SearchIndex.OP_AND;
		if("or".equals(keyjoin)) {
			joinop = SearchIndex.OP_OR;
		}

		for(Iterator ri = rset.iterator(); ri.hasNext();) {
			PhotoImageData pid = (PhotoImageData)ri.next();
			String info = pid.getDescr().toLowerCase();
			boolean matchedone = false;
			boolean matchedall = true;
			for(Iterator i = words.iterator(); i.hasNext();) {
				String word = (String)i.next();
				if(info.indexOf(word) >= 0) {
					matchedone = true;
				} else {
					matchedall = false;
				}
			}
			if(matchedone) {
				if(joinop == SearchIndex.OP_AND && !matchedall) {
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
		int which, String start, String end) throws Exception {

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
		for(Category cat : cf
			.getCatList(u.getId(), CategoryFactory.ACCESS_READ)) {
			validCats.add(cat.getId());
		}
		return (validCats);
	}

	private static abstract class PIDComparator
		implements Comparator<PhotoImageData> {
		public PIDComparator() {
			super();
		}

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

	private static class TimestampComparator extends PIDComparator {
		public TimestampComparator() {
			super();
		}

		public int doCompare(PhotoImageData pid1, PhotoImageData pid2) {
			return (pid1.getTimestamp().compareTo(pid2.getTimestamp()));
		}
	}

	private static class TakenComprator extends PIDComparator {
		public TakenComprator() {
			super();
		}

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

		public boolean equals(Object ob) {
			return (ob.getClass() == getClass());
		}

		public int compare(T ob1, T ob2) {
			return (0 - comp.compare(ob1, ob2));
		}
	}

}

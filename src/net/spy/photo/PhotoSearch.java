/*
 * Copyright (c) 1999  Dustin Sallings <dustin@spy.net>
 *
 * $Id: PhotoSearch.java,v 1.35 2003/05/27 03:36:22 dustin Exp $
 */

package net.spy.photo;

import java.util.Collection;
import java.util.Collections;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.TreeSet;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.Comparator;
import java.util.Date;

import java.net.URLEncoder;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.ServletException;

import net.spy.db.SpyDB;
import net.spy.util.Base64;

import net.spy.photo.struts.SearchForm;

import net.spy.photo.sp.InsertSearch;

/**
 * Perform searches.
 */
public class PhotoSearch extends PhotoHelper {

	private static final String CHARSET="UTF-8";

	private static final int BY_TS=1;
	private static final int BY_TAKEN=2;

	private static PhotoSearch instance=null;

	/**
	 * Get a PhotoSearch instance.
	 */
	private PhotoSearch() {
		super();
	}

	/** 
	 * Get an instance of Photosearch.
	 */
	public static synchronized PhotoSearch getInstance() {
		if(instance == null) {
			instance=new PhotoSearch();
		}
		return(instance);
	}

	/**
	 * Save a search.
	 */
	public void saveSearch(String name, String search, PhotoUser user)
		throws Exception {
		if(user==null || name==null || search==null) {
			throw new Exception("Weird, invalid stuff.");
		}

		if(!user.canAdd() ) {
			throw new Exception("No permission to save searches.");
		}

		try {
			InsertSearch is=new InsertSearch(getConfig());
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
		String rv=null;
		try {
			rv=URLEncoder.encode(msg, CHARSET);
		} catch(java.io.UnsupportedEncodingException e) {
			throw new RuntimeException(CHARSET + " is not supported.", e);
		}
		return(rv);
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
			String cats[]=form.getCat();
			for(int i=0; i<cats.length; i++) {
				sb.append("cat");
				sb.append('=');
				sb.append(urlEncode(cats[i]));
				sb.append('&');
			}
		}

		Base64 base64=new Base64();
		String out=base64.encode(sb.toString().getBytes());
		return(out);
	}

	public PhotoSearchResults performSearch(
		SearchForm form, PhotoSessionData sessionData)
		throws Exception {

		// Get the search index
		SearchIndex index=SearchIndex.getInstance();

		// Figure out how the thing should be sorted.
		Comparator comp=null;
		if("a.ts".equals(form.getOrder())) {
			comp=new TimestampComparator();
		} else {
			comp=new TakenComprator();
		}
		if("desc".equals(form.getSdirection())) {
			comp=new ReverseComparator(comp);
		}

		// This is the result set of images we want to display and maintain
		// their sort order
		Set rset=new TreeSet(comp);

		// Handle any category first
		String atmp[]=form.getCat();
		if(atmp != null && atmp.length > 0) {
			ArrayList al=new ArrayList(atmp.length);
			for(int i=0; i<atmp.length; i++) {
				al.add(new Integer(atmp[i]));
			}
			// Get rid of anything that's not valid for the current user
			al.retainAll(getValidCats(sessionData.getUser()));
			getLogger().info("Retaining images with cats " + al);
			rset.addAll(index.getForCats(al));
		} else {
			getLogger().info("Getting images for all categories");
			rset.addAll(index.getForCats(getValidCats(sessionData.getUser())));
		}

		// Check to see if there's a field entry
		if("keywords".equals(form.getField())) {
			processKeywords(rset, form.getWhat(), form.getKeyjoin());
		} else if("descr".equals(form.getField())) {
			throw new Exception("Not handling descr currently");
		} else {
			String stmp = form.getWhat();
			if(stmp != null && stmp.length() > 0) {
				throw new ServletException("Invalid field:  "
					+ form.getField());
			}
		}

		// Check for any of the date entries
		processDates(rset, form.getStart(), form.getEnd(),
			form.getTstart(), form.getTend());

		// Limit the results to the valid categories for the given user
		rset.retainAll(index.getForCats(getValidCats(sessionData.getUser())));

		// Populate the results
		PhotoSearchResults results=new PhotoSearchResults();
		results.setMaxSize(sessionData.getOptimalDimensions());
		int resultId=0;
		for(Iterator i=rset.iterator(); i.hasNext(); ) {
			PhotoImageData r=(PhotoImageData)i.next();
			results.add(new PhotoSearchResult(r, resultId));
			resultId++; 
		}

		return(results);
	}

	// In the case of dates, we do an explicit OR between the two date range
	// sets, and then AND the results back to current response set
	private void processDates(Set rset, String astart, String aend,
		String tstart, String tend) throws Exception {
		Collection aset=processRange(BY_TS, astart, aend);
		Collection tset=processRange(BY_TAKEN, tstart, tend);

		Set combined=new HashSet(aset);
		combined.addAll(tset);

		rset.retainAll(combined);
	}

	private void processKeywords(Set rset, String kw, String keyjoin)
		throws Exception {

		// Lookup the keywords
		ArrayList keywords=new ArrayList();
		if(kw == null) {
			kw="";
		}
		// Figure out the operation
		int joinop=SearchIndex.OP_AND;
		if("or".equals(keyjoin)) {
			joinop=SearchIndex.OP_OR;
		}
		// Flip through the keywords
		boolean missingKw=false;
		StringTokenizer st=new StringTokenizer(kw);
		while(st.hasMoreTokens()) {
			String kwstring=st.nextToken();
			Keyword k=Keyword.getKeyword(kwstring);
			if(k != null) {
				keywords.add(k);
			} else {
				getLogger().info("Unknown keyword:  " + kwstring);
				missingKw=true;
			}
		}
		if(joinop == SearchIndex.OP_AND && missingKw) {
			// If the joinop is AND and an invalid keyword was requested, just
			// clear the set, we're done
			getLogger().info("ANDing an unknown keyword");
			rset.clear();
		} else if(keywords.size() > 0) {
			// Remove everything that doesn't match our keywords (unless we
			// don't have any)
			getLogger().info("Got images with keywords " + keywords);
			SearchIndex index=SearchIndex.getInstance();
			Set keyset=index.getForKeywords(keywords, joinop);
			rset.retainAll(keyset);
		}
	}

	private Collection processRange(int which, String start, String end)
		throws Exception {

		SearchIndex index=SearchIndex.getInstance();
		Date s=PhotoUtil.parseDate(start);
		Date e=PhotoUtil.parseDate(end);

		Collection matches=Collections.EMPTY_LIST;

		if(s != null || e != null) {
			switch(which) {
				case BY_TS:
					matches=index.getForTimestamp(s, e);
					break;
				case BY_TAKEN:
					matches=index.getForTaken(s, e);
					break;
				default:
					throw new IllegalArgumentException("Invalid which:  "
						+ which);
			}
		}
		return(matches);
	}

	private Collection getValidCats(PhotoUser u) throws Exception {
		// Flip through all of the categories and get them as Integers
		Collection validCats=new ArrayList(16);
		for(Iterator i=Category.getCatList(
			u.getId(), Category.ACCESS_READ).iterator(); i.hasNext();) {
			Category cat=(Category)i.next();
			validCats.add(new Integer(cat.getId()));
		}
		return(validCats);
	}

	private static abstract class PIDComparator implements Comparator {
		public PIDComparator() {
			super();
		}
		public boolean equals(Object ob) {
			return(ob.getClass() == getClass());
		}

		protected abstract int doCompare(
			PhotoImageData pid1, PhotoImageData pid2);

		public int compare(Object ob1, Object ob2) {
			PhotoImageData pid1=(PhotoImageData)ob1;
			PhotoImageData pid2=(PhotoImageData)ob2;

			int rv=doCompare(pid1, pid2);
			if(rv == 0) {
				rv=(pid1.getId() - pid2.getId());
			}
			return(rv);
		}

	}

	private static class TimestampComparator extends PIDComparator {
		public TimestampComparator() {
			super();
		}

		public int doCompare(PhotoImageData pid1, PhotoImageData pid2) {
			return(pid1.getTimestamp().compareTo(pid2.getTimestamp()));
		}
	}

	private static class TakenComprator extends PIDComparator {
		public TakenComprator() {
			super();
		}

		public int doCompare(PhotoImageData pid1, PhotoImageData pid2) {
			return(pid1.getTaken().compareTo(pid2.getTaken()));
		}
	}

	private static class ReverseComparator implements Comparator {
		private Comparator comp=null;
		public ReverseComparator(Comparator c) {
			super();
			this.comp=c;
		}

		public boolean equals(Object ob) {
			return(ob.getClass() == getClass());
		}

		public int compare(Object ob1, Object ob2) {
			return(0 - comp.compare(ob1, ob2));
		}
	}

}

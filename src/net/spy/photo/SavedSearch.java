// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
// arch-tag: 4E3B5EA0-5D6D-11D9-A0C9-000A957659CC

package net.spy.photo;

import java.net.URLEncoder;

import java.util.Map;
import java.util.HashMap;
import java.util.TreeMap;

import java.io.UnsupportedEncodingException;

import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import net.spy.cache.SpyCache;
import net.spy.util.Base64;

import net.spy.photo.sp.GetSearches;
import net.spy.photo.struts.SearchForm;

/**
 * Represents a saved search entry.
 */
public class SavedSearch extends Object {

	private static final String CHARSET="UTF-8";

	private static final String CACHE_KEY="net.spy.photo.searches";
	private static final long CACHE_TIME=900000;

	private int id=0;
	private String name=null;
	private String search=null;

	/**
	 * Get an instance of SavedSearch.
	 */
	public SavedSearch() {
		super();
	}

	private SavedSearch(ResultSet rs) throws SQLException {
		Base64 base64=new Base64();
		id=rs.getInt("searches_id");
		name=rs.getString("name");
		byte data[]=base64.decode(rs.getString("search"));
		search=new String(data);
	}

	/** 
	 * Get the ID of this saved search.
	 */
	public int getId() {
		return(id);
	}

	/**
	 * Set the name of the search.
	 */
	public void setName(String name) {
		this.name=name;
	}

	/**
	 * Get the name of the search.
	 */
	public String getName() {
		return(name);
	}

	/**
	 * Set the actual search parameters.
	 */
	public void setSearch(String search) {
		this.search=search;
	}

	/**
	 * Get the search parameters.
	 */
	public String getSearch() {
		return(search);
	}

	/** 
	 * Get the search parameters, URL encoded.
	 */
	public String getSearchURL() throws UnsupportedEncodingException {
		return(URLEncoder.encode(search, CHARSET));
	}

	private static Map initSearchesMap() throws PhotoException {
		Map rv=null;
		try {
			GetSearches db=new GetSearches(PhotoConfig.getInstance());
			ResultSet rs=db.executeQuery();

			rv=new HashMap();
			while(rs.next()) {
				SavedSearch ss=new SavedSearch(rs);
				rv.put(new Integer(ss.id), ss);
			}
			rs.close();
			db.close();
		} catch(SQLException e) {
			throw new PhotoException("Problem loading searches", e);
		}

		return(rv);
	}

	private static Map getSearchesMap() throws PhotoException {
		SpyCache sc=SpyCache.getInstance();
		Map rv=(Map)sc.get(CACHE_KEY);
		if(rv == null) {
			rv=initSearchesMap();
			sc.store(CACHE_KEY, rv, CACHE_TIME);
		}
		return(rv);
	}

	/**
	 * Get the current set of saved searches (cached 15 minutes).
	 */
	public static Collection getSearches() throws PhotoException {
		TreeMap tm=new TreeMap();
		Map m=getSearchesMap();
		for(Iterator i=m.values().iterator();i.hasNext();) {
			SavedSearch ss=(SavedSearch)i.next();
			tm.put(ss.name, ss);
		}
		return(tm.values());
	}

	/** 
	 * Get a search by ID.
	 */
	public static SavedSearch getSearch(int id) throws PhotoException {
		Map m=getSearchesMap();
		SavedSearch ss=(SavedSearch)m.get(new Integer(id));
		if(ss == null) {
			throw new PhotoException("No such saved search:  " + id);
		}
		return(ss);
	}

	/**
	 * Test.
	 */
	public static void main(String argv[]) throws Exception {
		Collection c=getSearches();

		System.out.println("Saved searches:");
		for(Iterator i=c.iterator(); i.hasNext(); ) {
			SavedSearch s=(SavedSearch)i.next();
			System.out.println("\t" + s.getName() + " - " + s.getSearch());
		}
	}

}

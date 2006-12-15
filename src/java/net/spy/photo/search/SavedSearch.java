// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>

package net.spy.photo.search;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.beanutils.PropertyUtils;

import net.spy.cache.SimpleCache;
import net.spy.jwebkit.RequestUtil;
import net.spy.photo.PhotoConfig;
import net.spy.photo.PhotoException;
import net.spy.photo.sp.GetSearches;
import net.spy.photo.struts.SearchForm;
import net.spy.util.Base64;

/**
 * Represents a saved search entry.
 */
public class SavedSearch extends Object {

	private static final String CHARSET="UTF-8";

	static final String CACHE_KEY="net.spy.photo.searches";
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
	public void setName(String to) {
		this.name=to;
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
	public void setSearch(String to) {
		this.search=to;
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

	private static Map<Integer, SavedSearch> initSearchesMap()
		throws PhotoException {

		Map<Integer, SavedSearch> rv=null;
		try {
			GetSearches db=new GetSearches(PhotoConfig.getInstance());
			ResultSet rs=db.executeQuery();

			rv=new HashMap<Integer, SavedSearch>();
			while(rs.next()) {
				SavedSearch ss=new SavedSearch(rs);
				rv.put(ss.id, ss);
			}
			rs.close();
			db.close();
		} catch(SQLException e) {
			throw new PhotoException("Problem loading searches", e);
		}

		return(rv);
	}

	private static Map<Integer, SavedSearch> getSearchesMap()
		throws PhotoException {

		SimpleCache sc=SimpleCache.getInstance();
		@SuppressWarnings("unchecked")
		Map<Integer,SavedSearch> rv=(Map<Integer,SavedSearch>)sc.get(CACHE_KEY);
		if(rv == null) {
			rv=initSearchesMap();
			sc.store(CACHE_KEY, rv, CACHE_TIME);
		}
		return(rv);
	}

	/**
	 * Get the current set of saved searches (cached 15 minutes).
	 */
	public static Collection<SavedSearch> getSearches() throws PhotoException {
		TreeMap<String, SavedSearch> tm=new TreeMap<String, SavedSearch>();
		Map<Integer, SavedSearch> m=getSearchesMap();
		for(SavedSearch ss : m.values()) {
			tm.put(ss.name, ss);
		}
		return(tm.values());
	}

	/** 
	 * Get a search by ID.
	 */
	public static SavedSearch getSearch(int id) throws PhotoException {
		Map<Integer, SavedSearch> m=getSearchesMap();
		SavedSearch ss=m.get(id);
		if(ss == null) {
			throw new PhotoException("No such saved search:  " + id);
		}
		return(ss);
	}

	/**
	 * Get a SearchForm object representing this saved search.
	 */
	public SearchForm getSearchForm() throws Exception {
		SearchForm sf=new SearchForm();
		Map<String, String[]> m=RequestUtil.parseQueryString(
				getSearch(), CHARSET);

		// Stuff that should be copied directly.
		String straightCopy[]={
				"field", "keyjoin", "what", "what", "tstart", "tend", "start",
				"end", "order", "sdirection", "maxret", "filter", "action"
		};
		for(int i=0; i<straightCopy.length; i++) {
			String vs[]=m.get(straightCopy[i]);
			if(vs != null) {
				String v=vs[0];
				PropertyUtils.setProperty(sf, straightCopy[i], v);
			}
		}
		String vs[]=m.get("cats");
		if(vs != null) {
			PropertyUtils.setProperty(sf, "cats", vs);
		}

		return(sf);
	}

	@Override
	public int hashCode() {
		return id;
	}

	@Override
	public boolean equals(Object o) {
		boolean rv=false;
		if(o instanceof SavedSearch) {
			SavedSearch ss=(SavedSearch)o;
			rv=(ss.getId() == id);
		}
		return rv;
	}
}

// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: SavedSearch.java,v 1.3 2002/07/10 03:38:08 dustin Exp $

package net.spy.photo;

import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import net.spy.db.SpyCacheDB;

import net.spy.util.Base64;

/**
 * Represents a saved search entry.
 */
public class SavedSearch extends Object {

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
		name=rs.getString("name");
		byte data[]=base64.decode(rs.getString("search"));
		search=new String(data);
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
	 * Get the current set of saved searches (cached 15 minutes).
	 */
	public static Collection getSearches() throws SQLException {
		SpyCacheDB db=new SpyCacheDB(new PhotoConfig());
		ResultSet rs=db.executeQuery("select * from searches order by name",
			900);

		ArrayList l=new ArrayList();
		while(rs.next()) {
			l.add(new SavedSearch(rs));
		}
		rs.close();
		db.close();

		return(l);
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

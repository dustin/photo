/*
 * Copyright (c) 1999  Dustin Sallings <dustin@spy.net>
 *
 * $Id: PhotoSearch2.java,v 1.1 2002/05/13 07:22:48 dustin Exp $
 */

package net.spy.photo;

import java.io.*;
import java.sql.*;
import java.util.*;
import java.net.*;

import javax.servlet.*;
import javax.servlet.http.*;

import net.spy.*;
import net.spy.db.*;
import net.spy.util.*;

import net.spy.photo.struts.SearchForm;

/**
 * New PhotoSearch, making use of the struts bean rather than servlet
 * stuff.
 */
public class PhotoSearch2 extends PhotoHelper {

	/**
	 * Get a PhotoSearch2 instance.
	 */
	public PhotoSearch2() {
		super();
	}

	// Encode the search from the form stuff.
	public String encodeSearch(HttpServletRequest request) {
		String out = "";
		for(Enumeration e=request.getParameterNames(); e.hasMoreElements();) {
			String param=(String)e.nextElement();
			String values[] = request.getParameterValues(param);

			for(int i = 0; i < values.length; i++) {
				if(values[i].length()>0) {
					out+=URLEncoder.encode(param)+"="
						+ URLEncoder.encode(values[i]) +"&";
				}
			}
		}
		Base64 base64=new Base64();
		out=base64.encode(out.getBytes());
		return(out);
	}

	/**
	 * Save the search.
	 */
	public void saveSearch(SearchForm form, PhotoUser user)
		throws PhotoException {

		throw new PhotoException("saveSearch not implemented.");
	}

	// Save the search.
	/*
	public void saveSearch(SearchForm form, PhotoUser user)
		throws PhotoException {
		if(user==null || form==null) {
			throw new PhotoException("Weird, invalid stuff.");
		}

		if( ! user.canAdd() ) {
			throw new PhotoException("No permission to save searches.");
		}

		try {
			String stmp=null, name=null, search=null;

			name=request.getParameter("name").trim();
			if(name.length() == 0) {
				throw new Exception("Invalid ``name'' parameter");
			}

			search=request.getParameter("search").trim();
			if(search.length() == 0) {
				throw new Exception("Invalid ``search'' parameter");
			}

			String query = "insert into searches (name, addedby, search, ts)\n"
				+ "  values(?, ?, ?, ?)";
			SpyDB photo=new SpyDB(new PhotoConfig());
			PreparedStatement st=photo.prepareStatement(query);
			st.setString(1, name);
			st.setInt(2, user.getId());
			st.setString(3, search);
			st.setDate(4, new java.sql.Date(System.currentTimeMillis()));

			st.executeUpdate();

			photo.close();
		} catch(Exception e) {
			log("Error saving search:  " + e);
		}
	}
	*/

	/**
	 * Perform a search.
	 */
	public PhotoSearchResults performSearch(
		SearchForm form, PhotoSessionData sessionData)
		throws ServletException {

		PhotoSearchResults results=new PhotoSearchResults("PhotoServlet");
		results.setMaxSize(sessionData.getOptimalDimensions());

		try {

			// Go get a query
			String query=buildQuery(form, sessionData.getUser().getId());

			// Cache this query for fifteen minutes.  It's unique to the
			// user, but the user is often guest.
			SpyDB photo=new SpyDB(new PhotoConfig());
			ResultSet rs = photo.executeQuery(query);

			// Figure out how many they want to display.
			String tmp=form.getMaxret();
			if(tmp!=null) {
				int rv=Integer.parseInt(tmp);
				results.setMaxRet(rv);
			}

			int result_id=0;

			while(rs.next()) {
				int photo_id=rs.getInt(7);

				if(result_id<results.getMaxRet()) {
					// Fully populate the first ten search results.
					PhotoSearchResult r=new PhotoSearchResult();
					r.setKeywords(rs.getString(1));
					r.setDescr(   rs.getString(2));
					r.setCat(     rs.getString(3));
					r.setSize(    rs.getString(4));
					r.setTaken(   rs.getString(5));
					r.setTs(      rs.getString(6));
					r.setImage(   rs.getString(7));
					r.setCatNum(  rs.getString(8));
					r.setAddedBy( rs.getString(9));
					r.setWidth( rs.getString(11));
					r.setHeight( rs.getString(12));
					r.calculateThumbnailSize();
					// Add it to our search result set.
					results.add(r);
				} else {
					// The remaining search results just reference their IDs
					Integer i=new Integer(rs.getInt(7));
					results.add(i);
				}
				// Counting results...
				result_id++;
			}
			photo.close();
		} catch(Exception e) {
			throw new ServletException("Error performing search", e);
		}
		return(results);
	}

	// Build the bigass complex search query.
	private String buildQuery(SearchForm form, int remote_uid)
		throws ServletException {
		String query="", sub="", stmp="", order="",
			odirection="", fieldjoin="", join="";
		boolean needao=false;
		String atmp[];

		query = "select distinct a.keywords,a.descr,b.name,\n"
			+ " a.size,a.taken,a.ts,a.id,a.cat,c.username,b.id,\n"
			+ " a.width,a.height\n"
			+ "   from album a, cat b, wwwusers c, wwwacl acl\n"
			+ "       where a.cat=b.id\n"
			+ "       and a.addedby=c.id\n"
			+ "       and a.cat = acl.cat\n"
			+ "       and acl.canview=true\n"
			+ "       and ( acl.userid=" + remote_uid + "\n"
			+ "             or acl.userid= " + PhotoUtil.getDefaultId() + ")\n";

		// Find out what the fieldjoin is real quick...
		stmp=form.getFieldjoin();
		if(stmp == null) {
			fieldjoin="and";
		} else {
			fieldjoin=PhotoUtil.dbquote_str(stmp);
		}

		// Start with categories.
		atmp=form.getCat();

		if(atmp != null) {
			stmp="";
			boolean snao=false;

			// Do we need and or or?
			if(needao) {
				sub += " and";
			}
			needao=true;

			for(int i=0; i<atmp.length; i++) {
				if(snao) {
					stmp += " or";
				} else {
					snao=true;
				}
				stmp += "\n        a.cat=" + Integer.valueOf(atmp[i]);
			}

			if(atmp.length > 1) {
				sub += "\n     (" + stmp + "\n     )";
			} else {
				sub += "\n   " + stmp;
			}
		}

		// OK, lets look for search strings now...
		stmp = form.getWhat();
		if(stmp != null && stmp.length() > 0) {
			String a="", b="", field=null;
			boolean needjoin=false;

			// If we need an and or an or, stick it in here.
			if(needao) {
				sub += " " + fieldjoin;
			}
			needao=true;

			atmp = PhotoUtil.split(" ", stmp);

			join = PhotoUtil.dbquote_str(form.getKeyjoin());
			// Default
			if(join == null) {
				join = "or";
			}

			field = PhotoUtil.dbquote_str(form.getField());
			// Default
			if(field == null) {
				throw new ServletException("No field");
			}

			if(atmp.length > 1) {
				sub += "\n     (";
				for(int i=0; i<atmp.length; i++) {
					if(needjoin) {
						sub += join;
					} else {
						needjoin=true;
					}
					sub += "\n\t" + field + " ~* '"
						+ PhotoUtil.dbquote_str(atmp[i]) + "' ";
				}
				sub += "\n     )";
			} else {
				sub += "\n    " + field + " ~* '"
					+ PhotoUtil.dbquote_str(stmp) + "' ";
			}
		}

		// Starts and ends

		stmp=PhotoUtil.dbquote_str(form.getTstart());
		if(stmp != null && stmp.length()>0) {
			if(needao) {
				join=PhotoUtil.dbquote_str(form.getTstartjoin());
				if(join==null) {
					join="and";
				}
				sub += " " + join;
			}
			needao=true;
			sub += "\n    a.taken>='" + stmp + "'";
		}

		stmp=PhotoUtil.dbquote_str(form.getTend());
		if(stmp != null && stmp.length()>0) {
			if(needao) {
				join=PhotoUtil.dbquote_str(form.getTendjoin());
				if(join==null) {
					join="and";
				}
				sub += " " + join;
			}
			needao=true;
			sub += "\n    a.taken<='" + stmp + "'";
		}

		stmp=PhotoUtil.dbquote_str(form.getStart());
		if(stmp != null && stmp.length()>0) {
			if(needao) {
				join=PhotoUtil.dbquote_str(form.getStartjoin());
				if(join==null) {
					join="and";
				}
				sub += " " + join;
			}
			needao=true;
			sub += "\n    a.ts>='" + stmp + "'";
		}

		stmp=PhotoUtil.dbquote_str(form.getEnd());
		if(stmp != null && stmp.length()>0) {
			if(needao) {
				join=PhotoUtil.dbquote_str(form.getEndjoin());
				if(join==null) {
					join="and";
				}
				sub += " " + join;
			}
			needao=true;
			sub += "\n    a.ts<='" + stmp + "'";
		}

		// Stick the subquery on the bottom.
		if(sub.length() > 0 ) {
			query += " and\n (" + sub + "\n )";
		}

		// Figure out the direction...
		stmp=PhotoUtil.dbquote_str(form.getSdirection());
		if(stmp != null) {
			odirection=stmp;
		} else {
			odirection = "";
		}

		// Stick the order under the subquery.
		stmp=PhotoUtil.dbquote_str(form.getOrder());
		if(stmp != null) {
			if(stmp.equals("a.taken")) {
				order="a.taken " + odirection + ", a.ts " + odirection;
			} else {
				// If it's ordered by timestamp, include the album ID in
				// the sort, just in case.
				order="a.ts " + odirection
					+ ", a.taken " + odirection
					+ ", a.id " + odirection;
			}
		} else {
			order = "a.taken " + odirection + ", a.ts " + odirection;
		}

		query += "\n order by " + order;

		System.err.println("Searching with the following query:\n" + query);

		return(query);
	}
}

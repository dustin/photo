/*
 * Copyright (c) 1999  Dustin Sallings <dustin@spy.net>
 *
 * $Id: PhotoSearch.java,v 1.35 2003/05/27 03:36:22 dustin Exp $
 */

package net.spy.photo;

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

	/**
	 * Get a PhotoSearch instance.
	 */
	public PhotoSearch() {
		super();
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

		if(form.getFieldjoin() != null) {
			sb.append("fieldjoin");
			sb.append('=');
			sb.append(urlEncode(form.getFieldjoin()));
			sb.append('&');
		}
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
		if(form.getTstartjoin() != null) {
			sb.append("tstartjoin");
			sb.append('=');
			sb.append(urlEncode(form.getTstartjoin()));
			sb.append('&');
		}
		if(form.getTstart() != null) {
			sb.append("tstart");
			sb.append('=');
			sb.append(urlEncode(form.getTstart()));
			sb.append('&');
		}
		if(form.getTendjoin() != null) {
			sb.append("tendjoin");
			sb.append('=');
			sb.append(urlEncode(form.getTendjoin()));
			sb.append('&');
		}
		if(form.getTend() != null) {
			sb.append("tend");
			sb.append('=');
			sb.append(urlEncode(form.getTend()));
			sb.append('&');
		}
		if(form.getStartjoin() != null) {
			sb.append("startjoin");
			sb.append('=');
			sb.append(urlEncode(form.getStartjoin()));
			sb.append('&');
		}
		if(form.getStart() != null) {
			sb.append("start");
			sb.append('=');
			sb.append(urlEncode(form.getStart()));
			sb.append('&');
		}
		if(form.getEndjoin() != null) {
			sb.append("endjoin");
			sb.append('=');
			sb.append(urlEncode(form.getEndjoin()));
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

	/**
	 * Perform a search from a SearchForm.
	 */
	public PhotoSearchResults performSearch(
		SearchForm form, PhotoSessionData sessionData)
		throws ServletException {

		PhotoSearchResults results=new PhotoSearchResults();
		results.setMaxSize(sessionData.getOptimalDimensions());

		try {

			// Go get a query
			String query=buildQuery(form, sessionData.getUser().getId());

			// Cache this query for fifteen minutes.  It's unique to the
			// user, but the user is often guest.
			SpyDB photo=new SpyDB(getConfig());
			ResultSet rs = photo.executeQuery(query);

			// Figure out how many they want to display.
			String tmp=form.getMaxret();
			if(tmp!=null) {
				int rv=Integer.parseInt(tmp);
				results.setMaxRet(rv);
			}

			int resultId=0;

			while(rs.next()) {
				int photoId=rs.getInt(7);

				if(resultId<results.getMaxRet()) {
					// Fully populate the first few search results.
					PhotoImageData r=PhotoImageDataImpl.getData(photoId);
					// Add it to our search result set.
					results.add(new PhotoSearchResult(r, resultId));
				} else {
					// The remaining search results just reference their IDs
					Integer i=new Integer(rs.getInt(7));
					results.add(i);
				}
				// Counting results...
				resultId++;
			}
			photo.close();
		} catch(Exception e) {
			throw new ServletException("Error performing search", e);
		}
		if(getLogger().isDebugEnabled()) {
			getLogger().debug("performSearch returning " + results);
		}
		return(results);
	}

	// Build the bigass complex search query from a SearchForm
	private String buildQuery(SearchForm form, int remoteUid)
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
			+ "       and ( acl.userid=" + remoteUid + "\n"
			+ "             or acl.userid= " + PhotoUtil.getDefaultId() + ")\n";

		// Find out what the fieldjoin is real quick...
		stmp=form.getFieldjoin();
		if(stmp == null) {
			fieldjoin="and";
		} else {
			fieldjoin=PhotoUtil.dbquoteStr(stmp);
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
			String field=null;
			boolean needjoin=false;

			// If we need an and or an or, stick it in here.
			if(needao) {
				sub += " " + fieldjoin;
			}
			needao=true;

			atmp = PhotoUtil.split(" ", stmp);

			join = PhotoUtil.dbquoteStr(form.getKeyjoin());
			// Default
			if(join == null) {
				join = "or";
			}

			field = PhotoUtil.dbquoteStr(form.getField());
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
						+ PhotoUtil.dbquoteStr(atmp[i]) + "' ";
				}
				sub += "\n     )";
			} else {
				sub += "\n    " + field + " ~* '"
					+ PhotoUtil.dbquoteStr(stmp) + "' ";
			}
		}

		// Starts and ends

		stmp=PhotoUtil.dbquoteStr(form.getTstart());
		if(stmp != null && stmp.length()>0) {
			if(needao) {
				join=PhotoUtil.dbquoteStr(form.getTstartjoin());
				if(join==null) {
					join="and";
				}
				sub += " " + join;
			}
			needao=true;
			sub += "\n    a.taken>='" + stmp + "'";
		}

		stmp=PhotoUtil.dbquoteStr(form.getTend());
		if(stmp != null && stmp.length()>0) {
			if(needao) {
				join=PhotoUtil.dbquoteStr(form.getTendjoin());
				if(join==null) {
					join="and";
				}
				sub += " " + join;
			}
			needao=true;
			sub += "\n    a.taken<='" + stmp + "'";
		}

		stmp=PhotoUtil.dbquoteStr(form.getStart());
		if(stmp != null && stmp.length()>0) {
			if(needao) {
				join=PhotoUtil.dbquoteStr(form.getStartjoin());
				if(join==null) {
					join="and";
				}
				sub += " " + join;
			}
			needao=true;
			sub += "\n    a.ts>='" + stmp + "'";
		}

		stmp=PhotoUtil.dbquoteStr(form.getEnd());
		if(stmp != null && stmp.length()>0) {
			if(needao) {
				join=PhotoUtil.dbquoteStr(form.getEndjoin());
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
		stmp=PhotoUtil.dbquoteStr(form.getSdirection());
		if(stmp != null) {
			odirection=stmp;
		} else {
			odirection = "";
		}

		// Stick the order under the subquery.
		stmp=PhotoUtil.dbquoteStr(form.getOrder());
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

		if(getLogger().isDebugEnabled()) {
			getLogger().debug("Searching with the following query:\n" + query);
		}

		return(query);
	}

}

/*
 * Copyright (c) 1999  Dustin Sallings <dustin@spy.net>
 *
 * $Id: PhotoSearch.java,v 1.15 2001/12/28 13:13:41 dustin Exp $
 */

package net.spy.photo;

import java.io.*;
import java.sql.*;
import java.util.*;
import java.net.*;

import javax.servlet.*;
import javax.servlet.http.*;

import net.spy.*;
import net.spy.util.*;

public class PhotoSearch extends PhotoHelper {

	public PhotoSearch() throws Exception {
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

	// Save the search.
	public void saveSearch(HttpServletRequest request, PhotoUser user)
		throws Exception {
		if(user==null || request==null) {
			throw new Exception("Weird, invalid stuff.");
		}

		if( ! user.canAdd() ) {
			throw new Exception("No permission to save searches.");
		}

		Connection photo=null;

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
			photo=getDBConn();
			PreparedStatement st=photo.prepareStatement(query);
			st.setString(1, name);
			st.setInt(2, user.getId());
			st.setString(3, search);
			st.setDate(4, new java.sql.Date(System.currentTimeMillis()));

			st.executeUpdate();
		} catch(Exception e) {
			log("Error saving search:  " + e);
		} finally {
			if(photo!=null) {
				freeDBConn(photo);
			}
		}
	}

	// Actually perform the search
	public PhotoSearchResults performSearch(
		HttpServletRequest request, PhotoSessionData sessionData)
		throws ServletException {

		PhotoSearchResults results=
			new PhotoSearchResults(request.getRequestURI());
		results.setMaxSize(sessionData.getOptimalDimensions());

		Connection photo=null;

		try {

			// Go get a query
			String query=buildQuery(request, sessionData.getUser().getId());

			photo=getDBConn();
			Statement st=photo.createStatement();
			ResultSet rs = st.executeQuery(query);

			// Figure out how many they want to display.
			String tmp=request.getParameter("maxret");
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
					r.setTnWidth( rs.getString(13));
					r.setTnHeight( rs.getString(14));
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

		} catch(Exception e) {
			throw new ServletException("Error performing search", e);
		} finally {
			if(photo!=null) {
				freeDBConn(photo);
			}
		}
		return(results);
	}

	// Build the bigass complex search query.
	protected String buildQuery(HttpServletRequest request, int remote_uid)
		throws ServletException {
		String query="", sub="", stmp="", order="",
			odirection="", fieldjoin="", join="";
		boolean needao=false;
		String atmp[];

		query = "select a.keywords,a.descr,b.name,\n"
			+ " a.size,a.taken,a.ts,a.id,a.cat,c.username,b.id,\n"
			+ " a.width,a.height,a.tn_width,a.tn_height\n"
			+ "   from album a, cat b, wwwusers c\n   where a.cat=b.id\n"
			+ "       and a.addedby=c.id\n"
			+ "       and a.cat in (select cat from wwwacl\n"
			+ "              where canview=true and\n"
			+ "                    (userid=" + remote_uid + " or userid="
			+ PhotoUtil.getDefaultId() + "))";

		// Find out what the fieldjoin is real quick...
		stmp=request.getParameter("fieldjoin");
		if(stmp == null) {
			fieldjoin="and";
		} else {
			fieldjoin=PhotoUtil.dbquote_str(stmp);
		}

		// Start with categories.
		atmp=request.getParameterValues("cat");

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
		stmp = request.getParameter("what");
		if(stmp != null && stmp.length() > 0) {
			String a="", b="", field=null;
			boolean needjoin=false;

			// If we need an and or an or, stick it in here.
			if(needao) {
				sub += " " + fieldjoin;
			}
			needao=true;

			atmp = PhotoUtil.split(" ", stmp);

			join = PhotoUtil.dbquote_str(request.getParameter("keyjoin"));
			// Default
			if(join == null) {
				join = "or";
			}

			field = PhotoUtil.dbquote_str(request.getParameter("field"));
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

		stmp=PhotoUtil.dbquote_str(request.getParameter("tstart"));
		if(stmp != null && stmp.length()>0) {
			if(needao) {
				join=PhotoUtil.dbquote_str(request.getParameter("tstartjoin"));
				if(join==null) {
					join="and";
				}
				sub += " " + join;
			}
			needao=true;
			sub += "\n    a.taken>='" + stmp + "'";
		}

		stmp=PhotoUtil.dbquote_str(request.getParameter("tend"));
		if(stmp != null && stmp.length()>0) {
			if(needao) {
				join=PhotoUtil.dbquote_str(request.getParameter("tendjoin"));
				if(join==null) {
					join="and";
				}
				sub += " " + join;
			}
			needao=true;
			sub += "\n    a.taken<='" + stmp + "'";
		}

		stmp=PhotoUtil.dbquote_str(request.getParameter("start"));
		if(stmp != null && stmp.length()>0) {
			if(needao) {
				join=PhotoUtil.dbquote_str(request.getParameter("startjoin"));
				if(join==null) {
					join="and";
				}
				sub += " " + join;
			}
			needao=true;
			sub += "\n    a.ts>='" + stmp + "'";
		}

		stmp=PhotoUtil.dbquote_str(request.getParameter("end"));
		if(stmp != null && stmp.length()>0) {
			if(needao) {
				join=PhotoUtil.dbquote_str(request.getParameter("endjoin"));
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
		stmp=PhotoUtil.dbquote_str(request.getParameter("sdirection"));
		if(stmp != null) {
			odirection=stmp;
		} else {
			odirection = "";
		}

		// Stick the order under the subquery.
		stmp=PhotoUtil.dbquote_str(request.getParameter("order"));
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

		return(query);
	}
}

/*
 * Copyright (c) 2000  Dustin Sallings <dustin@spy.net>
 *
 * $Id: PhotoSearchResult.java,v 1.4 2000/07/04 22:48:31 dustin Exp $
 */

package net.spy.photo;

import java.sql.*;
import java.util.Hashtable;

import javax.servlet.*;
import javax.servlet.http.*;

import net.spy.*;

public class PhotoSearchResult extends PhotoHelper {
	protected String keywords=null;
	protected String descr=null;
	protected String cat=null;
	protected String size=null;
	protected String taken=null;
	protected String ts=null;
	protected String image=null;
	protected String catnum=null;
	protected String addedby=null;

	protected int id=-1;

	protected int user_id=-1;

	/**
	 * Get an uninitialized search result.
	 */
	public PhotoSearchResult() throws Exception {
		super();
	};

	/**
	 * Get an uninitialized search result pointing at a given id.
	 */
	public PhotoSearchResult(int id) throws Exception {
		super();
		this.id=id;
	};

	/**
	 * String representation of this object.
	 */
	public String toString() {
		return("Photo search result for result " + id);
	}

	/**
	 * Place Strings describing yourself into this hash.
	 *
	 * @param h Hashtable to inject.  If the passed in hash is null, it
	 * will create a new one.
	 *
	 * @return the Hashtable with all the stuff in it
	 */
	public Hashtable addToHash(Hashtable h) {
		if(h==null) {
			h=new Hashtable();
		}

		// Make sure we have data
		initialize();

		h.put("KEYWORDS", keywords);
		h.put("DESCR",    descr);
		h.put("CAT",      cat);
		h.put("SIZE",     size);
		h.put("TAKEN",    taken);
		h.put("TS",       ts);
		h.put("IMAGE",    image);
		h.put("CATNUM",   catnum);
		h.put("ADDEDBY",  addedby);
		h.put("ID",       "" + id);
		return(h);
	}

	protected void initialize() {
		// If we are uninitialized, but have an ID, initialize.
		if(id>=0
			&& keywords==null
			&& descr==null
			&& cat==null
			&& size==null
			&& taken==null
			&& ts==null
			&& image==null
			&& catnum==null
			&& addedby==null) {
			
			try {
				find(id);
			} catch(Exception e) {
				log("Error getting data for result " + id);
			}
		}
	}

	/**
	 * Populate myself via a database lookup.
	 *
	 * @param id Image ID
	 * @param uid User ID
	 *
	 * @throws Exception on failure
	 */
	public void find(int id, int uid) throws Exception {
		this.id=id;

		Connection photo=null;
		Exception ex=null;

		try {
			String query= "select a.id,a.keywords,a.descr,\n"
            	+ "   a.size,a.taken,a.ts,b.name,a.cat,c.username,b.id\n"
            	+ "   from album a, cat b, wwwusers c\n"
            	+ "   where a.cat=b.id and a.id=?\n"
            	+ "   and a.addedby=c.id\n"
            	+ "   and a.cat in (select cat from wwwacl where "
            	+ "userid=?)\n";
			photo=getDBConn();
			PreparedStatement st=photo.prepareStatement(query);
			st.setInt(1, id);
			st.setInt(2, uid);
			ResultSet rs=st.executeQuery();

			// Store it.
			storeResult(rs);

		} catch(Exception e) {
			// We'll throw this a bit later.
			ex=e;
		} finally {
			if(photo!=null) {
				freeDBConn(photo);
			}
		}
		// If we had an exception, get rid of it!
		if(ex!=null) {
			throw ex;
		}
	}

	protected void find(int id) throws Exception {
		this.id=id;

		Connection photo=null;
		Exception ex=null;

		try {
			String query= "select a.id,a.keywords,a.descr,\n"
            	+ "   a.size,a.taken,a.ts,b.name,a.cat,c.username,b.id\n"
            	+ "   from album a, cat b, wwwusers c\n"
            	+ "   where a.cat=b.id and a.id=?\n"
            	+ "   and a.addedby=c.id\n";
			photo=getDBConn();
			PreparedStatement st=photo.prepareStatement(query);
			st.setInt(1, id);
			ResultSet rs=st.executeQuery();
			// Store it.
			storeResult(rs);
		} catch(Exception e) {
			// Save it to be thrown later.
			ex=e;
		} finally {
			if(photo!=null) {
				freeDBConn(photo);
			}
		}
		// If we had an exception, toss it up
		if(ex!=null) {
			throw ex;
		}
	}

	protected void storeResult(ResultSet rs) throws Exception {

		// If there's not a result, error
		if(!rs.next()) {
			throw new Exception("No result received for " + id);
		}
		int i=1;
		// Grab the components
		image=rs.getString(i++);
		keywords=rs.getString(i++);
		descr=rs.getString(i++);
		size=rs.getString(i++);
		taken=rs.getString(i++);
		ts=rs.getString(i++);
		cat=rs.getString(i++);
		catnum=rs.getString(i++);
		addedby=rs.getString(i++);
		// If there's another result error
		if(rs.next()) {
			throw new Exception("Too many results received for " + id);
		}
	}

	public void setKeywords(String to) {
		keywords=to;
	}

	public void setDescr(String to) {
		descr=to;
	}

	public void setCat(String to) {
		cat=to;
	}

	public void setSize(String to) {
		size=to;
	}

	public void setTaken(String to) {
		taken=to;
	}

	public void setTs(String to) {
		ts=to;
	}

	public void setImage(String to) {
		image=to;
	}

	public void setCatNum(String to) {
		catnum=to;
	}

	public void setAddedBy(String to) {
		addedby=to;
	}

	public void setId(int id) {
		this.id=id;
	}
}

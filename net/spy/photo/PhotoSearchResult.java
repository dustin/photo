/*
 * Copyright (c) 2000  Dustin Sallings <dustin@spy.net>
 *
 * $Id: PhotoSearchResult.java,v 1.14 2001/04/29 08:18:11 dustin Exp $
 */

package net.spy.photo;

import java.sql.*;
import java.util.Hashtable;
import java.util.Enumeration;
import java.io.Serializable;

import javax.servlet.*;
import javax.servlet.http.*;

import net.spy.*;
import net.spy.cache.*;

public class PhotoSearchResult extends PhotoHelper implements Serializable {
	protected Hashtable mydata=null;
	protected int id=-1;
	protected int search_id=-1;
	protected String html=null;
	protected String xml=null;

	/**
	 * Get an uninitialized search result.
	 */
	public PhotoSearchResult() throws Exception {
		super();
		mydata=new Hashtable();
	}

	/**
	 * Get an uninitialized search result pointing at a given id.
	 */
	public PhotoSearchResult(int id, int search_id) throws Exception {
		super();
		this.id=id;
		this.search_id=search_id;
	}

	/**
	 * String representation of this object.
	 */
	public String toString() {
		String out="Photo search result for result ";
		if(id>0) {
			out+=id;
		} else {
			out+=search_id;
		}
		return(out);
	}

	/**
	 * Grab the XML chunk to be displayed.
	 */
	public String showXML(String self_uri) {
		if(xml==null) {
			// Initialize the xml thingy.
			xml="";
			// Make sure we have the data.
			addToHash(null);
			for(Enumeration e=mydata.keys(); e.hasMoreElements(); ) {
				String key=(String)e.nextElement();
				String data=(String)mydata.get(key);

				xml+="<" + key + ">" + data + "</" + key + ">\n";
			}
		}
		return(xml);
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

		for(Enumeration e=mydata.keys(); e.hasMoreElements(); ) {
			Object k=e.nextElement();
			h.put(k, mydata.get(k));
		}

		mydata.put("ID",       "" + search_id);
		h.put("ID",            "" + search_id);

		return(h);
	}

	protected void initialize() {
		// If we are uninitialized, but have an ID, initialize.
		if(id>=0 && mydata==null) {
			try {
				SpyCache pc=new SpyCache();
				mydata=(Hashtable)pc.get("s_result_" + id);
				if(mydata==null) {
					find(id);
					// Store it for fifteen minutes.
					pc.store("s_result_" + id, mydata, 600*1000);
				}
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
            	+ "   a.size,a.taken,a.ts,b.name,a.cat,c.username,b.id,\n"
				+ "   a.width, a.height, a.tn_width, a.tn_height\n"
            	+ "   from album a, cat b, wwwusers c\n"
            	+ "   where a.cat=b.id and a.id=?\n"
            	+ "   and a.addedby=c.id\n"
            	+ "   and a.cat in (select cat from wwwacl where "
            	+ "userid=? or userid=?)\n";
			photo=getDBConn();
			PreparedStatement st=photo.prepareStatement(query);
			st.setInt(1, id);
			st.setInt(2, uid);
			st.setInt(3, PhotoUtil.getDefaultId());
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
            	+ "   a.size,a.taken,a.ts,b.name,a.cat,c.username,b.id,\n"
				+ "   a.width, a.height, a.tn_width, a.tn_height\n"
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

		// If we don't already have a result hash, build one.
		if(mydata==null) {
			mydata=new Hashtable();
		}

		// Grab the components
		int i=1;
		mydata.put("IMAGE",    rs.getString(i++));
		mydata.put("KEYWORDS", rs.getString(i++));
		mydata.put("DESCR",    rs.getString(i++));
		mydata.put("SIZE",     rs.getString(i++));
		mydata.put("TAKEN",    rs.getString(i++));
		mydata.put("TS",       rs.getString(i++));
		mydata.put("CAT",      rs.getString(i++));
		mydata.put("CATNUM",   rs.getString(i++));
		mydata.put("ADDEDBY",  rs.getString(i++));
		i++; // skip this one
		mydata.put("WIDTH",    rs.getString(i++));
		mydata.put("HEIGHT",   rs.getString(i++));
		mydata.put("TN_WIDTH",    rs.getString(i++));
		mydata.put("TN_HEIGHT",   rs.getString(i++));

		// If there's another result error
		if(rs.next()) {
			throw new Exception("Too many results received for " + id);
		}
	}

	public void setKeywords(String to) {
		mydata.put("KEYWORDS", to);
	}

	public void setDescr(String to) {
		mydata.put("DESCR", to);
	}

	public void setCat(String to) {
		mydata.put("CAT", to);
	}

	public void setSize(String to) {
		mydata.put("SIZE", to);
	}

	public void setTaken(String to) {
		mydata.put("TAKEN", to);
	}

	public void setTs(String to) {
		mydata.put("TS", to);
	}

	public void setImage(String to) {
		mydata.put("IMAGE", to);
	}

	public void setCatNum(String to) {
		mydata.put("CATNUM", to);
	}

	public void setAddedBy(String to) {
		mydata.put("ADDEDBY", to);
	}

	public void setWidth(String to) {
		mydata.put("WIDTH", to);
	}

	public void setHeight(String to) {
		mydata.put("HEIGHT", to);
	}

	public void setTnWidth(String to) {
		mydata.put("TN_WIDTH", to);
	}

	public void setTnHeight(String to) {
		mydata.put("TN_HEIGHT", to);
	}

	public void setId(int id) {
		this.search_id=id;
	}

	public int getCatNum() {
		initialize();
		int ret=Integer.parseInt((String)mydata.get("CATNUM"));
		return(ret);
	}
}

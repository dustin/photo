/*
 * Copyright (c) 2000  Dustin Sallings <dustin@spy.net>
 *
 * $Id: PhotoSearchResult.java,v 1.2 2000/06/30 00:09:59 dustin Exp $
 */

package net.spy.photo;

import java.sql.*;

import javax.servlet.*;
import javax.servlet.http.*;

import net.spy.*;

public class PhotoSearchResult extends PhotoHelper {
	public String keywords=null;
	public String descr=null;
	public String cat=null;
	public String size=null;
	public String taken=null;
	public String ts=null;
	public String image=null;
	public String catnum=null;
	public String addedby=null;

	public int id=-1;

	public PhotoSearchResult() throws Exception {
		super();
	};

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
}

// Copyright (c) 2000  Dustin Sallings <dustin@spy.net>

package net.spy.photo.migration;

import java.sql.*;
import net.spy.*;
import net.spy.photo.*;

/**
 * This is the base class for migration utilities.
 */
public abstract class PhotoMigration extends Object {
	public boolean tryQuery(String query) throws Exception {
		boolean ret=false;

		// Make sure we can do a query.
		SpyDB db=new SpyDB(new PhotoConfig());
		ResultSet rs=db.executeQuery("select 1");
		rs.next();

		try {
			rs=db.executeQuery(query);
			rs.next();
			ret=true;
		} catch(Exception e) {
			// Ignore errors, we just want to know if that would have worked.
		}

		return(ret);
	}

	// See if we have a needed column
	public boolean hasColumn(String table, String column) throws Exception {
		return(tryQuery("select " + column + " from" + table + " where 1=2"));
	}

	// Fetch all thumbnails.
	public void fetchThumbnails() throws Exception {
		SpyDB db=new SpyDB(new PhotoConfig());
		ResultSet rs=db.executeQuery("select id from album order by ts desc");
		while(rs.next()) {
			int id=rs.getInt(1);
			System.out.println("Doing image #" + id);
			PhotoImageHelper helper=new PhotoImageHelper(id);
			PhotoImage image=helper.getThumbnail();
			System.out.println("Done.");
		}
	}
}

// Copyright (c) 2000  Dustin Sallings <dustin@spy.net>

package net.spy.photo.migration;

import java.sql.*;
import java.io.*;
import java.net.URL;
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
		rs.close();

		try {
			rs=db.executeQuery(query);
			rs.next();
			ret=true;
		} catch(Exception e) {
			// Ignore errors, we just want to know if that would have worked.
		}

		db.close();

		return(ret);
	}

	private static URL findPath(String rel)
		throws FileNotFoundException {
		// Just need some object that will be loaded near the photo stuff
		PhotoConfig conf=new PhotoConfig();
		ClassLoader cl=conf.getClass().getClassLoader();
		URL u=cl.getResource(rel);
		if(u==null) {
			throw new FileNotFoundException("Can't find " + rel);
		}
		return(u);
	}

	protected void runSqlScript(String path) throws Exception {
		URL u=findPath(path);

		System.out.println("Running SQL script from " + u);

		SpyDB db=new SpyDB(new PhotoConfig());
		Connection conn=db.getConn();
		conn.setAutoCommit(false);

		LineNumberReader lr=new LineNumberReader(
			new InputStreamReader(u.openStream()));

		try {
			String curline=null;
			StringBuffer query=new StringBuffer();
			while( (curline=lr.readLine()) != null) {
				curline=curline.trim();

				if(curline.equals(";")) {
					Statement st=conn.createStatement();
					long starttime=System.currentTimeMillis();
					int updated=st.executeUpdate(query.toString());
					long stoptime=System.currentTimeMillis();
					st.close();
					st=null;
					String rows=" rows";
					if(updated==1) {
						rows=" row";
					}
					System.out.println("Updated " + updated + rows + " in "
						+ (stoptime-starttime) + "ms");
					query=new StringBuffer();
				} else if(curline.startsWith("--")) {
					System.out.println(lr.getLineNumber() + ": " + curline);
				} else {
					if(curline.length()>0) {
						query.append(curline);
						query.append("\n");
					}
				}

			}

			conn.commit();
		} catch(Exception e) {
			conn.rollback();
			throw e;
		} finally {
			conn.setAutoCommit(true);
			db.close();
		}

		lr.close();
	}

	// See if we have a needed column
	public boolean hasColumn(String table, String column) throws Exception {
		return(tryQuery("select " + column + " from " + table + " where 1=2"));
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

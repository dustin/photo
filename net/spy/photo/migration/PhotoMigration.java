// Copyright (c) 2000  Dustin Sallings <dustin@spy.net>

package net.spy.photo.migration;

import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;

import java.net.URL;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import net.spy.SpyDB;

import net.spy.photo.PhotoConfig;
import net.spy.photo.PhotoImage;
import net.spy.photo.PhotoImageHelper;

/**
 * This is the base class for migration utilities.
 */
public abstract class PhotoMigration extends Object {

	/**
	 * Try a query and see whether it would succeed.  This is used for
	 * testing tables, views, etc...
	 */
	protected boolean tryQuery(String query) throws Exception {
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
		} catch(SQLException e) {
			// Ignore errors, we just want to know if that would have worked.
		}

		db.close();

		return(ret);
	}

	// Find a URL to a file by class-ish name.
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

	/**
	 * Run the given script as a transaction.
	 *
	 * @param path The relative path to the migration sql script.
	 */
	protected void runSqlScript(String path) throws Exception {
		runSqlScript(path, false, false);
	}

	/**
	 * Run a SQL script with the ability to run it as a transaction and
	 * ignore errors.  You may not ignore errors if the script is
	 * transactional.
	 *
	 * @param path The relative path to the migration sql script.
	 * @param autocommit If false, the script will run as a single transaction.
	 * @param errok If true, errors will be reported, but the script will
	 *				continue.
	 */
	protected void runSqlScript(String path, boolean autocommit,
		boolean errok) throws Exception {

		URL u=findPath(path);

		if(autocommit==false && errok==true) {
			throw new Exception("Can't ignore errors on autocommit.");
		}

		System.out.println("Running SQL script from " + u);

		SpyDB db=new SpyDB(new PhotoConfig());
		Connection conn=db.getConn();
		conn.setAutoCommit(autocommit);

		LineNumberReader lr=new LineNumberReader(
			new InputStreamReader(u.openStream()));

		try {
			String curline=null;
			StringBuffer query=new StringBuffer();
			while( (curline=lr.readLine()) != null) {
				curline=curline.trim();

				if(curline.equals(";")) {
					Statement st=conn.createStatement();
					int updated=0;
					long starttime=System.currentTimeMillis();
					try {
						updated=st.executeUpdate(query.toString());
					} catch(SQLException se) {
						if(errok) {
							System.err.println("Query:\n" + query);
							se.printStackTrace();
							System.err.println("Continuing...");
						} else {
							throw se;
						}
					}
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

			if(!autocommit) {
				conn.commit();
			}
		} catch(Exception e) {
			if(!autocommit) {
				conn.rollback();
			}
			throw e;
		} finally {
			conn.setAutoCommit(true);
			db.close();
		}

		lr.close();
	}

	/**
	 * True if the given table has the given column.
	 */
	protected boolean hasColumn(String table, String column) throws Exception {
		return(tryQuery("select " + column + " from " + table + " where 1=2"));
	}

	/**
	 * Perform the migration.
	 */
	public abstract void migrate() throws Exception;

	/**
	 * Fetch all thumbnails.  This is used to populate caches and stuff
	 * like that.
	 */
	protected void fetchThumbnails() throws Exception {
		SpyDB db=new SpyDB(new PhotoConfig());
		ResultSet rs=db.executeQuery("select id from album order by ts desc");
		while(rs.next()) {
			int id=rs.getInt(1);
			System.out.println("Doing image #" + id);
			PhotoImageHelper helper=new PhotoImageHelper(id);
			PhotoImage image=helper.getThumbnail();
			if(image==null) {
				throw new Exception("Why did that return null?");
			}
			System.out.println("Done.");
		}
	}
}

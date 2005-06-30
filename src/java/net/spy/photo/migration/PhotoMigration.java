// Copyright (c) 2000  Dustin Sallings <dustin@spy.net>
// arch-tag: 64FC0D28-5D6D-11D9-A80C-000A957659CC

package net.spy.photo.migration;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import net.spy.SpyObject;
import net.spy.db.SQLRunner;
import net.spy.db.SpyDB;
import net.spy.photo.PhotoConfig;
import net.spy.photo.PhotoImage;
import net.spy.photo.PhotoImageHelper;

/**
 * This is the base class for migration utilities.
 */
public abstract class PhotoMigration extends SpyObject {

	/**
	 * Try a query and see whether it would succeed.  This is used for
	 * testing tables, views, etc...
	 */
	protected boolean tryQuery(String query) throws Exception {
		boolean ret=false;

		// Make sure we can do a query.
		SpyDB db=new SpyDB(PhotoConfig.getInstance());
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

	/** 
	 * Get the row count for the given table.
	 * 
	 * @param table the given table
	 * @return the number of rows in the given table.
	 */
	protected int getRowCount(String table) throws Exception {
		int rv=0;
		SpyDB db=new SpyDB(PhotoConfig.getInstance());
		ResultSet rs=db.executeQuery("select count(*) from " + table);
		if(!rs.next()) {
			throw new Exception("No rows returned!?");
		}
		rv=rs.getInt(1);
		rs.close();
		db.close();
		return(rv);
	}

	// Find a URL to a file by class-ish name.
	private static URL findPath(String rel)
		throws FileNotFoundException {
		// Just need some object that will be loaded near the photo stuff
		PhotoConfig conf=PhotoConfig.getInstance();
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

		SpyDB db=new SpyDB(PhotoConfig.getInstance());
		Connection conn=db.getConn();

		InputStream is=u.openStream();

		SQLRunner sr=new SQLRunner(conn);
		sr.runScript(is, autocommit, errok);

		is.close();
	}

	/**
	 * True if the given table has the given column.
	 */
	protected boolean hasColumn(String table, String column) throws Exception {
		return(tryQuery("select " + column + " from " + table + " where 1=2"));
	}

	/** 
	 * Check to see if we've already run this migration kit.
	 * 
	 * @return true if this kit has already been run.
	 */
	protected abstract boolean checkMigration() throws Exception;

	/** 
	 * Perform this migration.
	 */
	protected abstract void performMigration() throws Exception;

	/**
	 * Perform the migration.
	 */
	public final void migrate() throws Exception {
		if(checkMigration()) {
			System.err.println("Looks like you've already run "
				+ getClass().getName());
		} else {
			performMigration();
		}
	}

	/**
	 * Fetch all thumbnails.  This is used to populate caches and stuff
	 * like that.
	 */
	protected void fetchThumbnails() throws Exception {
		SpyDB db=new SpyDB(PhotoConfig.getInstance());
		ResultSet rs=db.executeQuery("select id from album order by ts desc");
		while(rs.next()) {
			int id=rs.getInt(1);
			getLogger().debug("Doing image #" + id);
			PhotoImageHelper helper=new PhotoImageHelper(id);
			PhotoImage image=helper.getThumbnail();
			if(image==null) {
				throw new Exception("Why did that return null?");
			}
			getLogger().debug("Done.");
		}
	}
}

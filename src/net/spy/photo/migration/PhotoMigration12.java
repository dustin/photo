// Copyright (c) 2002  Dustin Sallings <dustin@spy.net>
//
// $Id: PhotoMigration12.java,v 1.1 2002/09/13 20:28:46 dustin Exp $

package net.spy.photo.migration;

/**
 * Drop old keywords column.
 */
public class PhotoMigration12 extends PhotoMigration
{

	/**
	 * Get an instance of PhotoMigration12.
	 */
	public PhotoMigration12() {
		super();
	}

	/** 
	 * Perform the migration.
	 */
	public void migrate() throws Exception {
		if(!hasColumn("album", "keywords")) {
			System.err.println("Looks like you've already run this kit.");
		} else {
			runSqlScript("net/spy/photo/migration/migration12.sql");
		}
	}

	/** 
	 * Run the 9th migration script.
	 */
	public static void main(String args[]) throws Exception {
		PhotoMigration12 mig=new PhotoMigration12();
		mig.migrate();
	}

}

// Copyright (c) 2002  Dustin Sallings <dustin@spy.net>
//
// $Id: PhotoMigration08.java,v 1.1 2002/09/13 20:28:46 dustin Exp $

package net.spy.photo.migration;

/**
 * Add properties table.
 */
public class PhotoMigration08 extends PhotoMigration
{

	/**
	 * Get an instance of PhotoMigration08.
	 */
	public PhotoMigration08() {
		super();
	}

	/** 
	 * Perform the migration.
	 */
	public void migrate() throws Exception {
		if(hasColumn("properties", "name")) {
			System.err.println("Looks like you've already run this kit.");
		} else {
			runSqlScript("net/spy/photo/migration/migration08.sql");
		}
	}

	/** 
	 * Run the 8th migration script.
	 */
	public static void main(String args[]) throws Exception {
		PhotoMigration08 mig=new PhotoMigration08();
		mig.migrate();
	}

}

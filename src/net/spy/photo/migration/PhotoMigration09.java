// Copyright (c) 2002  Dustin Sallings <dustin@spy.net>
//
// $Id: PhotoMigration09.java,v 1.1 2002/09/13 20:28:46 dustin Exp $

package net.spy.photo.migration;

/**
 * Add properties table.
 */
public class PhotoMigration09 extends PhotoMigration
{

	/**
	 * Get an instance of PhotoMigration09.
	 */
	public PhotoMigration09() {
		super();
	}

	/** 
	 * Perform the migration.
	 */
	public void migrate() throws Exception {
		if(hasColumn("wwwusers", "persess")) {
			System.err.println("Looks like you've already run this kit.");
		} else {
			runSqlScript("net/spy/photo/migration/migration09.sql");
		}
	}

	/** 
	 * Run the 9th migration script.
	 */
	public static void main(String args[]) throws Exception {
		PhotoMigration09 mig=new PhotoMigration09();
		mig.migrate();
	}

}

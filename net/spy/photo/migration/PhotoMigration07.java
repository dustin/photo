// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: PhotoMigration07.java,v 1.1 2002/08/14 06:59:08 dustin Exp $

package net.spy.photo.migration;

/**
 * Add keyword mappings.
 */
public class PhotoMigration07 extends PhotoMigration {

	/**
	 * Get an instance of PhotoMigration07.
	 */
	public PhotoMigration07() {
		super();
	}

	/**
	 * Perform the migration.
	 */
	public void migrate() throws Exception {
		if(hasColumn("keywords", "word_id")) {
			System.err.println("Looks like you've already run this kit.");
		} else {
			runSqlScript("net/spy/photo/migration/migration07.sql");
		}
	}

	/**
	 * Migrate.
	 */
	public static void main(String args[]) throws Exception {
		PhotoMigration07 mig=new PhotoMigration07();
		mig.migrate();
	}

}

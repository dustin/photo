// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: PhotoMigration06.java,v 1.2 2002/07/10 03:38:08 dustin Exp $

package net.spy.photo.migration;

/**
 * Add galleries.
 */
public class PhotoMigration06 extends PhotoMigration {

	/**
	 * Get an instance of PhotoMigration06.
	 */
	public PhotoMigration06() {
		super();
	}

	public void migrate() throws Exception {
		if(hasColumn("galleries", "gallery_name")) {
			System.err.println("Looks like you've already run this kit.");
		} else {
			runSqlScript("net/spy/photo/migration/migration06.sql");
		}
	}

	/**
	 * Testing and what not.
	 */
	public static void main(String args[]) throws Exception {
		PhotoMigration06 mig=new PhotoMigration06();
		mig.migrate();
	}

}



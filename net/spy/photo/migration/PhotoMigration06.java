// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: PhotoMigration06.java,v 1.1 2002/06/30 07:51:31 dustin Exp $

package net.spy.photo.migration;

import net.spy.photo.*;

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


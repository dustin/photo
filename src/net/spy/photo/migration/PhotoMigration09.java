// Copyright (c) 2002  Dustin Sallings <dustin@spy.net>
//
// $Id: PhotoMigration09.java,v 1.1 2002/09/13 20:28:46 dustin Exp $

package net.spy.photo.migration;

/**
 * Add the ``persess'' column to wwwusers.
 */
public class PhotoMigration09 extends PhotoMigration {

	/**
	 * Get an instance of PhotoMigration09.
	 */
	public PhotoMigration09() {
		super();
	}

	protected boolean checkMigration() throws Exception {
		return(hasColumn("wwwusers", "persess"));
	}

	protected void performMigration() throws Exception {
		runSqlScript("net/spy/photo/migration/migration09.sql");
	}

	public static void main(String args[]) throws Exception {
		PhotoMigration09 mig=new PhotoMigration09();
		mig.migrate();
	}

}

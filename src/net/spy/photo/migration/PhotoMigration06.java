// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: PhotoMigration06.java,v 1.3 2002/08/14 06:59:08 dustin Exp $

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

	protected boolean checkMigration() throws Exception {
		return(hasColumn("galleries", "gallery_name"));
	}

	protected void performMigration() throws Exception {
		runSqlScript("net/spy/photo/migration/migration06.sql");
	}

	public static void main(String args[]) throws Exception {
		PhotoMigration06 mig=new PhotoMigration06();
		mig.migrate();
	}

}

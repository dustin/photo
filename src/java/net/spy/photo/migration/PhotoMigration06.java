// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
// arch-tag: 6EC94DC0-5D6D-11D9-8F49-000A957659CC

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

	@Override
	protected boolean checkMigration() throws Exception {
		return(hasColumn("galleries", "gallery_name"));
	}

	@Override
	protected void performMigration() throws Exception {
		runSqlScript("net/spy/photo/migration/migration06.sql");
	}

	public static void main(String args[]) throws Exception {
		PhotoMigration06 mig=new PhotoMigration06();
		mig.migrate();
	}

}

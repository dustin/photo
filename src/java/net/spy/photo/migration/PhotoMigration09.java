// Copyright (c) 2002  Dustin Sallings <dustin@spy.net>
// arch-tag: 751E719A-5D6D-11D9-856F-000A957659CC

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

	@Override
	protected boolean checkMigration() throws Exception {
		return(hasColumn("wwwusers", "persess"));
	}

	@Override
	protected void performMigration() throws Exception {
		runSqlScript("net/spy/photo/migration/migration09.sql");
	}

	public static void main(String args[]) throws Exception {
		PhotoMigration09 mig=new PhotoMigration09();
		mig.migrate();
	}

}

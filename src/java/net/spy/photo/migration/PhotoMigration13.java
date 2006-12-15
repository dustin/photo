// Copyright (c) 2002  Dustin Sallings <dustin@spy.net>

package net.spy.photo.migration;

/**
 * Add region mapping.
 */
public class PhotoMigration13 extends PhotoMigration {

	/**
	 * Get an instance of PhotoMigration13.
	 */
	public PhotoMigration13() {
		super();
	}

	@Override
	protected boolean checkMigration() throws Exception {
		return(hasColumn("region", "region_id"));
	}

	@Override
	protected void performMigration() throws Exception {
		runSqlScript("net/spy/photo/migration/migration13.sql");
	}

	public static void main(String args[]) throws Exception {
		PhotoMigration13 mig=new PhotoMigration13();
		mig.migrate();
	}

}

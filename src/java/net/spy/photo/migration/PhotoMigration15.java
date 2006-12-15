// Copyright (c) 2006  Dustin Sallings <dustin@spy.net>

package net.spy.photo.migration;

/**
 * Add geocoding.
 */
public class PhotoMigration15 extends PhotoMigration {

	/**
	 * Get an instance of PhotoMigration15.
	 */
	public PhotoMigration15() {
		super();
	}

	@Override
	protected boolean checkMigration() throws Exception {
		return(hasColumn("place", "place_id"));
	}

	@Override
	protected void performMigration() throws Exception {
		runSqlScript("net/spy/photo/migration/migration15.sql");
	}

	public static void main(String args[]) throws Exception {
		PhotoMigration15 mig=new PhotoMigration15();
		mig.migrate();
	}

}

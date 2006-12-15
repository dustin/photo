// Copyright (c) 2002  Dustin Sallings <dustin@spy.net>

package net.spy.photo.migration;

/**
 * Add properties table.
 */
public class PhotoMigration08 extends PhotoMigration {

	/**
	 * Get an instance of PhotoMigration08.
	 */
	public PhotoMigration08() {
		super();
	}

	@Override
	protected boolean checkMigration() throws Exception {
		return(hasColumn("properties", "name"));
	}

	@Override
	protected void performMigration() throws Exception {
		runSqlScript("net/spy/photo/migration/migration08.sql");
	}

	public static void main(String args[]) throws Exception {
		PhotoMigration08 mig=new PhotoMigration08();
		mig.migrate();
	}

}

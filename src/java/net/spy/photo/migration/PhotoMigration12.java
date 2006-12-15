// Copyright (c) 2002  Dustin Sallings <dustin@spy.net>

package net.spy.photo.migration;

/**
 * Drop old keywords column.
 */
public class PhotoMigration12 extends PhotoMigration {

	/**
	 * Get an instance of PhotoMigration12.
	 */
	public PhotoMigration12() {
		super();
	}

	@Override
	protected boolean checkMigration() throws Exception {
		return(!hasColumn("album", "keywords"));
	}

	@Override
	protected void performMigration() throws Exception {
		runSqlScript("net/spy/photo/migration/migration12.sql");
	}

	public static void main(String args[]) throws Exception {
		PhotoMigration12 mig=new PhotoMigration12();
		mig.migrate();
	}

}

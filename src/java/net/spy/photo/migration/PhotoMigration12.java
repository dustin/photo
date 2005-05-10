// Copyright (c) 2002  Dustin Sallings <dustin@spy.net>
// arch-tag: 77A28245-5D6D-11D9-8292-000A957659CC

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

	protected boolean checkMigration() throws Exception {
		return(!hasColumn("album", "keywords"));
	}

	protected void performMigration() throws Exception {
		runSqlScript("net/spy/photo/migration/migration12.sql");
	}

	public static void main(String args[]) throws Exception {
		PhotoMigration12 mig=new PhotoMigration12();
		mig.migrate();
	}

}

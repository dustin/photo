// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>

package net.spy.photo.migration;

/**
 * Add keyword mappings.
 */
public class PhotoMigration07 extends PhotoMigration {

	/**
	 * Get an instance of PhotoMigration07.
	 */
	public PhotoMigration07() {
		super();
	}

	@Override
	protected boolean checkMigration() throws Exception {
		return(hasColumn("keywords", "word_id"));
	}

	@Override
	protected void performMigration() throws Exception {
		runSqlScript("net/spy/photo/migration/migration07.sql");
	}

	public static void main(String args[]) throws Exception {
		PhotoMigration07 mig=new PhotoMigration07();
		mig.migrate();
	}

}

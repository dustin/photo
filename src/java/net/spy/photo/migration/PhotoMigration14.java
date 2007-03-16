// Copyright (c) 2006  Dustin Sallings <dustin@spy.net>

package net.spy.photo.migration;

/**
 * Add variants.
 */
public class PhotoMigration14 extends PhotoMigration {

	@Override
	protected boolean checkMigration() throws Exception {
		return(hasColumn("photo_variations", "variant_id"));
	}

	@Override
	protected void performMigration() throws Exception {
		runSqlScript("net/spy/photo/migration/migration14.sql");
	}

	public static void main(String args[]) throws Exception {
		PhotoMigration14 mig=new PhotoMigration14();
		mig.migrate();
	}

}

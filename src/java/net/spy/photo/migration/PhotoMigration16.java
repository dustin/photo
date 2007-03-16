// Copyright (c) 2006  Dustin Sallings <dustin@spy.net>

package net.spy.photo.migration;

/**
 * Add image MD5s.
 */
public class PhotoMigration16 extends PhotoMigration {

	@Override
	protected boolean checkMigration() throws Exception {
		return(hasColumn("album", "md5"));
	}

	@Override
	protected void performMigration() throws Exception {
		runSqlScript("net/spy/photo/migration/migration16.sql");
	}

	public static void main(String args[]) throws Exception {
		PhotoMigration16 mig=new PhotoMigration16();
		mig.migrate();
	}

}

// Copyright (c) 2000  Dustin Sallings <dustin@spy.net>

package net.spy.photo.migration;

/** 
 * Migration kit for enhancing logs.
 */
public class PhotoMigration05 extends PhotoMigration {

	protected boolean checkMigration() throws Exception {
		return( (hasColumn("photo_logs", "wwwuser_id"))
			&& (hasColumn("photo_logs", "log_id")));
	}

	protected void performMigration() throws Exception {
		runSqlScript("net/spy/photo/migration/migration05.sql");
		runSqlScript("net/spy/photo/migration/migration05.ac.sql", true, true);
	}

	public static void main(String args[]) throws Exception {
		PhotoMigration05 mig=new PhotoMigration05();
		mig.migrate();
	}
}


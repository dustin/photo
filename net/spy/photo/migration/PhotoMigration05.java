// Copyright (c) 2000  Dustin Sallings <dustin@spy.net>

package net.spy.photo.migration;

public class PhotoMigration05 extends PhotoMigration {

	public void migrate() throws Exception {
		if( (hasColumn("photo_logs", "wwwuser_id"))
			&& (hasColumn("photo_logs", "log_id"))) {
			System.err.println("Looks like you've already run this kit.");
		} else {
			runSqlScript("net/spy/photo/migration/migration05.sql");
			runSqlScript("net/spy/photo/migration/migration05.ac.sql",
				true, true);
		}
	}

	public static void main(String args[]) throws Exception {
		PhotoMigration05 mig=new PhotoMigration05();
		mig.migrate();
	}
}


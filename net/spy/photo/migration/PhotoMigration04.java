// Copyright (c) 2000  Dustin Sallings <dustin@spy.net>

package net.spy.photo.migration;

public class PhotoMigration04 extends PhotoMigration {

	public void migrate() throws Exception {
		if( (hasColumn("commentary", "comment_id"))
			&& (hasColumn("votes", "vote_id"))) {
			System.err.println("Looks like you've already run this kit.");
		} else {
			// Add the new columns.
			runSqlScript("net/spy/photo/migration/migration04.sql");
		}
	}

	public static void main(String args[]) throws Exception {
		PhotoMigration04 mig=new PhotoMigration04();
		mig.migrate();
	}
}


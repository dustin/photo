// Copyright (c) 2000  Dustin Sallings <dustin@spy.net>
// arch-tag: 6CCB5214-5D6D-11D9-8D07-000A957659CC

package net.spy.photo.migration;

/** 
 * Migration kit for adding commentary and votes.
 */
public class PhotoMigration04 extends PhotoMigration {

	protected boolean checkMigration() throws Exception {
		return( (hasColumn("commentary", "comment_id"))
			&& (hasColumn("votes", "vote_id")));
	}

	protected void performMigration() throws Exception {
		// Add the new columns.
		runSqlScript("net/spy/photo/migration/migration04.sql");
	}

	public static void main(String args[]) throws Exception {
		PhotoMigration04 mig=new PhotoMigration04();
		mig.migrate();
	}
}


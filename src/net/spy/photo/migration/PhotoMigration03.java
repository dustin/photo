// Copyright (c) 2000  Dustin Sallings <dustin@spy.net>
// arch-tag: 6A13E8DC-5D6D-11D9-A187-000A957659CC

package net.spy.photo.migration;

/** 
 * Migration kit for adding profiles.
 */
public class PhotoMigration03 extends PhotoMigration {

	protected boolean checkMigration() throws Exception {
		return( (hasColumn("user_profiles", "profile_id"))
			&& (hasColumn("user_profile_acls", "profile_id"))
			&& (hasColumn("user_profile_log", "profile_id")));
	}

	protected void performMigration() throws Exception {
		runSqlScript("net/spy/photo/migration/migration03.sql");
	}

	public static void main(String args[]) throws Exception {
		PhotoMigration03 mig=new PhotoMigration03();
		mig.migrate();
	}
}


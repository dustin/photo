// Copyright (c) 2000  Dustin Sallings <dustin@spy.net>

package net.spy.photo.migration;

import java.sql.*;
import net.spy.*;
import net.spy.photo.*;

public class PhotoMigration03 extends PhotoMigration {

	public void migrate() throws Exception {
		if( (hasColumn("user_profiles", "profile_id"))
			&& (hasColumn("user_profile_acls", "profile_id"))
			&& (hasColumn("user_profile_log", "profile_id")))
			System.err.println("Looks like you've already run this kit.");
		} else {
			runSqlScript("net/spy/photo/migration/migration03.sql");
		}
	}

	public static void main(String args[]) throws Exception {
		PhotoMigration03 mig=new PhotoMigration03();
		mig.migrate();
	}
}

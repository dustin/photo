// Copyright (c) 2000  Dustin Sallings <dustin@spy.net>

package net.spy.photo.migration;

import java.sql.*;
import net.spy.*;
import net.spy.photo.*;

public class PhotoMigration03 extends PhotoMigration {

	private void addTables() throws Exception {
		SpyDB db=new SpyDB(new PhotoConfig());
		try {
			// User profiles
			db.executeUpdate("create table user_profiles (\n"
				+ " profile_id serial,\n"
				+ " name varchar(32) not null,\n"
				+ " description text not null,\n"
				+ " expires date not null,\n"
				+ " primary key(profile_id)\n");
			db.executeUpdate("create unique index user_profilesbyname "
				+ "on user_profiles(name)");
			db.executeUpdate("grant all on user_profiles to nobody");
			db.executeUpdate("grant all on user_profiles_profile_id_seq "
				+ " to nobody");

			// Profile ACLs
			db.executeUpdate("create table user_profile_acls (\n"
				+ " profile_id integer not null,\n"
				+ " cat_id integer not null,\n"
				+ " foreign key(profile_id) references user_profiles(profile_id),\n"
				+ " foreign key(cat_id) references cat(id)\n");
			db.executeQuery("create index user_profile_aclsbyp \n"
				+ " on user_profile_acls(profile_id)");
			db.executeQuery("grant all on user_profile_acls to nobody");

			// Log
			db.executeQuery("create table user_profile_log (\n"
				+ " log_id serial,\n"
				+ " profile_id integer not null,\n"
				+ " wwwuser_id integer not null,\n"
				+ " ts timestamp default now(),\n"
				+ " remote_addr inet not null,\n"
				+ " primary key(log_id),\n"
				+ " foreign key(profile_id) references user_profiles(profile_id),\n"
				+ " foreign key(wwwuser_id) references wwwusers(id)");
			db.executeQuery("create index user_profile_log_byuser "
				+ " on user_profile_log(wwwuser_id)");
			db.executeQuery("create index user_profile_log_byprof "
				+ " on user_profile_log(profile_id)");
			db.executeQuery("grant all on user_profile_log_log_id_seq to nobody");
			db.executeQuery("grant all on user_profile_log to nobody");
		} catch(Exception e) {
			System.err.println("Error adding column:  " + e);
		}
		db.close();
	}

	public void migrate() throws Exception {
		if( hasColumn("user_profiles", "profile_id") ) {
			System.err.println("Looks like you've already run this kit.");
		} else {
			// Add the new columns.
			addTables();
		}
	}

	public static void main(String args[]) throws Exception {
		PhotoMigration03 mig=new PhotoMigration03();
		mig.migrate();
	}
}

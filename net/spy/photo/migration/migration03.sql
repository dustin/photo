-- New user profiles table
create table user_profiles (
	profile_id serial,
	name varchar(32) not null,
	description text not null,
	expires date not null,
	primary key(profile_id)
);
create unique index user_profilesbyname on user_profiles(name);
grant all on user_profiles to nobody;
grant all on user_profiles_profile_id_seq to nobody;

-- Profile ACLs
create table user_profile_acls (
	profile_id integer not null,
	cat_id integer not null,
	foreign key(profile_id) references user_profiles(profile_id),
	foreign key(cat_id) references cat(id)
);
create index user_profile_aclsbyp on user_profile_acls(profile_id);
grant all on user_profile_acls to nobody;

-- This table logs when users are created with profiles
create table user_profile_log (
	log_id serial,
	profile_id integer not null,
	wwwuser_id integer not null,
	ts timestamp default now(),
	remote_addr inet not null,
	primary key(log_id),
	foreign key(profile_id) references user_profiles(profile_id),
	foreign key(wwwuser_id) references wwwusers(id)
);
create index user_profile_log_byuser on user_profile_log(wwwuser_id);
create index user_profile_log_byprof on user_profile_log(profile_id);
grant all on user_profile_log_log_id_seq to nobody;
grant all on user_profile_log to nobody;

-- View to show the profile users along with the profiles that created them
create view user_byprofiles as
	select	wwwusers.id as user_id, wwwusers.username, wwwusers.realname,
			user_profiles.profile_id as profile,
			user_profiles.description as profile_desc,
			date(ts) as created
		from
			wwwusers, user_profiles, user_profile_log
		where
			wwwusers.id=user_profile_log.wwwuser_id
			and user_profiles.profile_id=user_profile_log.profile_id
		order by
			user_id
;
grant select on user_byprofiles to nobody;

-- View to show the profiles and the permissions they grant
create view user_profile_view as
	select p.name, p.description, p.expires,
			c.name as cat_name
		from
			user_profiles p, cat c, user_profile_acls a
		where
			p.profile_id=a.profile_id
			and c.id=a.cat_id
		order by
			p.expires
;
grant select on user_profile_view to nobody;

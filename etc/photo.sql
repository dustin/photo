-- Copyright (c) 1998  Dustin Sallings
--
-- $Id: photo.sql,v 1.31 2002/09/13 20:28:46 dustin Exp $
--
-- Use this to bootstrap your SQL database to do cool shite with the
-- photo album.

begin transaction;

-- add support for PL/pgsql

CREATE FUNCTION plpgsql_call_handler () RETURNS OPAQUE AS
	'/usr/local/pgsql/lib/plpgsql.so' LANGUAGE 'C';

CREATE TRUSTED PROCEDURAL LANGUAGE 'plpgsql'
	HANDLER plpgsql_call_handler
	LANCOMPILER 'PL/pgSQL';


-- The categories
create table cat(
	id   serial,
	name text not null,
	primary key(id)
);
grant all on cat to nobody;
-- implicit sequence
grant all on cat_id_seq to nobody;

-- Users go here
create table wwwusers(
	id       serial,
	username varchar(16) not null,
	password text not null,
	email    text not null,
	realname text not null,
	canadd   bool not null,
	primary key(id)
);
create unique index user_byname on wwwusers(username);
create unique index user_byemail on wwwusers(email);
grant all on wwwusers to nobody;
grant all on wwwusers_id_seq to nobody;

-- add guest and admin users
insert into wwwusers(username, password, email, realname, canadd)
	values('guest', '', 'photos@localhost', 'Guest User', false);
-- Default password for admin is ``admin''
insert into wwwusers(username, password, email, realname, canadd)
	values('admin', '0DPiKuNIrrVmD8IUCuw1hQxNqZc', 'photoadmin@localhost',
		'Admin User', true);
-- get a user ID from a username
create function getwwwuser(text) returns integer as
	'select id from wwwusers where username = $1'
	language 'sql';

-- Keywords
create table keywords (
	word_id serial,
	word varchar(32) not null,
	primary key(word_id)
);
create unique index keywords_byword on keywords(word);
grant all on keywords to nobody;
grant all on keywords_word_id_seq to nobody;

-- Where the picture info is stored.

create table album(
	keywords   varchar(50) not null,
	descr      text not null,
	cat        integer not null,
	taken      date not null,
	size       integer not null,
	addedby    integer not null,
	width      integer default 0,
	height     integer default 0,
	ts         datetime not null,
	id         serial,
	primary key(id),
	foreign key(cat) references cat(id),
	foreign key(addedby) references wwwusers(id)
);

create index album_bycat on album(cat);
grant all on album to nobody;
-- implicit sequence
grant all on album_id_seq to nobody;

-- keywords <-> photo mappings
create table album_keywords_map (
	album_id integer not null,
	word_id integer not null,
	foreign key(album_id) references album(id),
	foreign key(word_id) references keywords(word_id)
);
create unique index album_keywords_map_byu
	on album_keywords_map(album_id, word_id);
create index album_keywords_map_bya on album_keywords_map(album_id);
create index album_keywords_map_byw on album_keywords_map(word_id);
grant all on album_keywords_map to nobody;

-- Notes
create table commentary (
	comment_id serial,
	wwwuser_id integer not null,
	photo_id integer not null,
	note text not null,
	remote_addr inet not null,
	ts timestamp default now(),
	primary key(comment_id),
	foreign key(wwwuser_id) references wwwusers(id),
	foreign key(photo_id) references album(id)
);
create index commentary_byphoto on commentary(photo_id);
create index commentary_byuser on commentary(wwwuser_id);
grant all on commentary to nobody;
grant all on commentary_comment_id_seq to nobody;

-- Get the latest date a comment was submitted for a given photo
create function latestcomment(integer) returns timestamp as
	'select max(ts) from commentary where photo_id = $1'
	language 'sql';

-- Votes
create table votes (
	vote_id serial,
	wwwuser_id integer not null,
	photo_id integer not null,
	vote smallint not null,
	remote_addr inet not null,
	ts timestamp default now(),
	primary key(vote_id),
	foreign key(wwwuser_id) references wwwusers(id),
	foreign key(photo_id) references album(id)
);
create unique index votes_byui on votes(wwwuser_id, photo_id);
create index votes_byphoto on votes(photo_id);
create index votes_byuser on votes(wwwuser_id);
grant all on votes to nobody;
grant all on votes_vote_id_seq to nobody;

-- The ACLs for the categories

create table wwwacl(
	userid   integer not null,
	cat      integer not null,
	canview  boolean default true,
	canadd   boolean default false,
	foreign key(userid) references wwwusers(id),
	foreign key(cat) references cat(id)
);

create index acl_byid on wwwacl(userid);
create index acl_bycat on wwwacl(cat);
grant all on wwwacl to nobody;

-- view for showing acls by name

create view show_acl as
	select wwwusers.username, wwwacl.cat, cat.name, wwwacl.canview,
		wwwacl.canadd
	from wwwusers, wwwacl, cat
	where wwwusers.id=wwwacl.userid
	and wwwacl.cat=cat.id
;

grant all on show_acl to nobody;

-- The group file for the Web server's ACL crap.

create table wwwgroup(
	userid    integer not null,
	groupname varchar(16) not null,
	foreign key(userid) references wwwusers(id)
);

grant all on wwwgroup to nobody;

-- Add the admin user to the wwwgroup
insert into wwwgroup values(getwwwuser('admin'), 'admin');

create view show_group as
	select wwwusers.username, wwwgroup.groupname
	from wwwusers, wwwgroup
	where wwwusers.id=wwwgroup.userid
;

grant all on show_group to nobody;

-- Search saves

create table searches (
	searches_id	serial,
	name		text not null,
	addedby		integer not null,
	search		text not null,
	ts			datetime not null,
	primary key(searches_id),
	foreign key(addedby) references wwwusers(id)
);

grant all on searches to nobody;
-- implicit seqeunce
grant all on searches_searches_id_seq to nobody;

-- Hmm...  Store images in text?  OK, sure...
-- This is keyed of the id in the album table

create table image_store (
	id   integer not null,
	line integer not null,
	data text not null,
	foreign key(id) references album(id) on delete cascade
);

grant all on image_store to nobody;
create index images_id on image_store(id);

-- A SQL function to return the count of elements in a category.

create function catsum (integer)
	returns integer AS
	'select count(*) from album where cat = $1'
	language 'SQL';

-- User Agent table, for recording user-agents in logs.

create table user_agent (
	user_agent_id serial,
	user_agent text,
	primary key(user_agent_id)
);

grant all on user_agent to nobody;
grant all on user_agent_user_agent_id_seq to nobody;

create unique index user_agent_text on user_agent(user_agent);

create function get_agent(text) returns integer as
'
declare
	id integer;
begin
	select user_agent_id into id from user_agent where user_agent = $1;
	if not found then
		insert into user_agent(user_agent) values($1);
		select user_agent_id into id from user_agent where user_agent = $1;
	end if;
	return(id);
end;
' language 'plpgsql';

-- Log various activities

create table log_types (
	log_type_id serial,
	log_type varchar(32),
	primary key(log_type_id)
);
create unique index log_types_bytype on log_types(log_type);
grant all on log_types to nobody;
grant all on log_types_log_type_id_seq to nobody;

-- Insert some data.
insert into log_types(log_type) values('Login');
insert into log_types(log_type) values('ImgView');
insert into log_types(log_type) values('Upload');
insert into log_types(log_type) values('AuthFail');
insert into log_types(log_type) values('Request');

-- A function for looking up log types
create function get_log_type(TEXT) returns INTEGER as
	'select log_type_id from log_types where log_type = $1'
	language 'sql' with (iscachable)
;

create table photo_logs (
	log_id serial,
	log_type integer not null,
	wwwuser_id integer not null,
	photo_id integer,
	remote_addr inet not null,
	user_agent integer not null,
	extra_info text,
	ts datetime default now(),
	primary key(log_id),
	foreign key(log_type) references log_types(log_type_id),
	foreign key(wwwuser_id) references wwwusers(id),
	foreign key(user_agent) references user_agent(user_agent_id)
);
create index photo_logs_bytype on photo_logs(log_type);
create index photo_logs_byuser on photo_logs(wwwuser_id);
create index photo_logs_byphoto on photo_logs(photo_id);
grant all on photo_logs to nobody;
grant all on photo_logs_log_id_seq to nobody;

-- New user profiles
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

-- Table for galleries
create table galleries (
	gallery_id serial,
	gallery_name varchar(64) not null,
	wwwuser_id integer not null,
	ispublic boolean not null,
	ts timestamp default now(),
	primary key(gallery_id),
	foreign key(wwwuser_id) references wwwusers(id)
);
grant all on galleries to nobody;
grant all on galleries_gallery_id_seq to nobody;

-- The actual images stored in the galleries
create table galleries_map (
	gallery_id integer not null,
	album_id integer not null,
	foreign key(gallery_id) references galleries(gallery_id),
	foreign key(album_id) references album(id)
);
create index galleries_mapbygal on galleries_map(gallery_id);
create index galleries_mapbyalbum on galleries_map(album_id);
grant all on galleries_map to nobody;

-- General properties (photo of the unit of time, etc...)
create table properties (
	name varchar(32) not null,
	value text,
	primary key(name)
);
grant all on properties to nobody;
-- Add the photo of the unit of time property
insert into properties(name, value) values('photo_of_uot', '1');

-- Show the profile users along with the profiles that created them
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

-- View the profiles
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
grant select on user_profile_view to nobody
;

-- Log view
create view log_user_ip_agent as
	select u.username, l.ts, l.remote_addr, a.user_agent,
		l.extra_info as img_size
	from wwwusers u, photo_logs l, user_agent a, log_types t
	where u.id = l.wwwuser_id and
		a.user_agent_id = l.user_agent
		and l.log_type = t.log_type_id
		and t.log_type ='ImgView';
;

grant select on log_user_ip_agent to nobody
;

create view log_user_ip_keywords as
	select a.id as photo_id, u.username, l.remote_addr, a.keywords, l.ts
		from wwwusers u, photo_logs l, album a
	  where u.id = l.wwwuser_id and
	    a.id = l.photo_id
;

grant select on log_user_ip_keywords to nobody
;

-- For viewing auth logs
create view auth_log_view as
	select
		u.username, l.remote_addr, l.ts, t.log_type
	from
		wwwusers u, photo_logs l, log_types t
	where
		u.id=l.wwwuser_id
		and l.log_type = t.log_type_id
		and (t.log_type='Login' or t.log_type='AuthFail')
	order by
		l.ts desc
;
grant select on auth_log_view to nobody
;


-- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --

-- MISC QUERIES

-- Garbage collector, unfortunately, this will not work in a view.

-- select distinct id from image_store where id not in
--	(select id from album);

commit;

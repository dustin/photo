-- Copyright (c) 1998  Dustin Sallings
--
-- $Id: photo.sql,v 1.10 2001/12/28 12:39:37 dustin Exp $
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
	tn_width   integer default 0,
	tn_height  integer default 0,
	ts         datetime not null,
	id         serial
);

create index album_bycat on album(cat);
grant all on album to nobody;
-- implicit sequence
grant all on album_id_seq to nobody;

-- Get rid of all image_store data when an album entry is deleted:

create function tg_album_delete() returns opaque as
	'begin
		delete from image_store where id = OLD.id;
		return OLD;
	 end;'
	language 'plpgsql';

create trigger album_cleanup_tg before delete on album
	for each row execute procedure tg_album_delete();

-- The categories

create table cat(
	id   serial,
	name text not null
);

grant all on cat to nobody;
-- implicit sequence
grant all on cat_id_seq to nobody;

-- The passwd file for the Web server's ACL crap.

create table wwwusers(
	id       serial,
	username varchar(16) not null,
	password text not null,
	email    text not null,
	realname text not null,
	canadd   bool not null
);

create unique index user_byname on wwwusers(username);
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

-- The ACLs for the categories

create table wwwacl(
	userid   integer not null,
	cat      integer not null,
	canview  boolean default true,
	canadd   boolean default false
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
	groupname varchar(16) not null
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
	ts			datetime not null
);

grant all on searches to nobody;
-- implicit seqeunce
grant all on searches_searches_id_seq to nobody;

-- Hmm...  Store images in text?  OK, sure...
-- This is keyed of the id in the album table

create table image_store (
	id   integer not null,
	line integer not null,
	data text not null
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
	user_agent text
);

grant all on user_agent to nobody;

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

-- Log image retrievals.

create table photo_log (
	photo_id integer not null,
	wwwuser_id integer not null,
	remote_addr inet not null,
	server_host text not null,
	user_agent integer not null,
	cached boolean not null,
	ts datetime not null
);

grant all on photo_log to nobody;

create index photo_log_photo_id on photo_log(photo_id);
create index photo_log_wwwuser_id on photo_log(wwwuser_id);
create index photo_log_remote_addr on photo_log(remote_addr);

-- Log of uploaded images.

create table upload_log (
	photo_id integer not null,
	wwwuser_id integer not null,
	stored datetime,
	ts datetime not null
);

grant all on upload_log to nobody;
create unique index upload_log_photo on upload_log(photo_id);

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
	foreign key(profile_id) references user_profiles(profile_id)
);
create index user_profile_aclsbyp on user_profile_acls(profile_id);
grant all on user_profile_acls to nobody;

-- Log view
create view log_user_ip_agent as
	select wwwusers.username, photo_log.remote_addr, user_agent.user_agent
		from wwwusers, photo_log, user_agent
	  where wwwusers.id = photo_log.wwwuser_id and
	  	user_agent.user_agent_id = photo_log.user_agent
;

grant all on log_user_ip_agent to nobody;

create view log_user_ip_keywords as
	select wwwusers.username, photo_log.remote_addr, album.keywords
		from wwwusers, photo_log, album
	  where wwwusers.id = photo_log.wwwuser_id and
	    album.id = photo_log.photo_id
;

grant all on log_user_ip_keywords to nobody;

-- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --

-- MISC QUERIES

-- Garbage collector, unfortunately, this will not work in a view.

-- select distinct id from image_store where id not in
--	(select id from album);

commit;

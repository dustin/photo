
-- New table to define log types

create table log_types (
	log_type_id serial,
	log_type varchar(32),
	primary key(log_type_id)
)
;
create unique index log_types_bytype on log_types(log_type)
;
grant all on log_types to nobody
;
grant all on log_types_log_type_id_seq to nobody
;

-- Insert some log types

insert into log_types(log_type) values('Login')
;
insert into log_types(log_type) values('ImgView')
;
insert into log_types(log_type) values('Upload')
;
insert into log_types(log_type) values('AuthFail')
;

-- A function for looking up log types
create function get_log_type(TEXT) returns INTEGER as
	'select log_type_id from log_types where log_type = $1'
	language 'sql' with (iscachable)
;

-- New way to do logs

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
	foreign key(photo_id) references album(id) on delete set null,
	foreign key(user_agent) references user_agent(user_agent_id)
)
;
create index photo_logs_bytype on photo_logs(log_type)
;
create index photo_logs_byuser on photo_logs(wwwuser_id)
;
create index photo_logs_byphoto on photo_logs(photo_id)
;
grant all on photo_logs to nobody
;
grant all on photo_logs_log_id_seq to nobody
;

-- Insert all of the view logs

insert into photo_logs (log_type,wwwuser_id,photo_id,remote_addr,user_agent,ts)
	select t.log_type_id,
		l.wwwuser_id, l.photo_id, l.remote_addr, l.user_agent, l.ts
	from
		photo_log l, log_types t
	where
		t.log_type='ImgView'
;

-- Create a temporary table for guessing the IP address a user was using
-- while uploading images.

select
	distinct on(wwwuser_id, month)
		wwwuser_id, remote_addr, date_trunc('month', ts) as month
into
	tmpaddrtable
from
	photo_log
;
create unique index tmpad_idx on tmpaddrtable(wwwuser_id, month)
;

-- Create a temporary table for guessing the browser a user was using
-- while uploading images.

select
	distinct on(wwwuser_id, month)
		wwwuser_id, user_agent, date_trunc('month', ts) as month
into
	tmpagenttable
from
	photo_log
;
create unique index tmpag_idx on tmpagenttable(wwwuser_id, month)
;

-- Quick pass to make sure there's nothing that needed to be set to null
-- by the null settin' trigger
update upload_log
	set photo_id = null
		where photo_id not in (select id from album)
;

-- Insert all of the old upload logs

insert into photo_logs
	(log_type,wwwuser_id,photo_id,remote_addr,user_agent,extra_info,ts)
	select t.log_type_id,
		l.wwwuser_id, l.photo_id,
		ad.remote_addr,
		ag.user_agent,
		text(stored),
		l.ts
	from
		upload_log l,
		log_types t,
		tmpagenttable ag,
		tmpaddrtable ad
	where
		t.log_type='Upload'
		and ad.wwwuser_id=l.wwwuser_id
		and ad.month=date_trunc('month', l.ts)
		and ag.wwwuser_id=l.wwwuser_id
		and ag.month=date_trunc('month', l.ts)
;

-- Drop temporary tables

drop table tmpaddrtable
;
drop table tmpagenttable
;

-- Dropping old log tables

--   Dropping photo_log
drop table photo_log;
--   Dropping upload_log
drop table upload_log;

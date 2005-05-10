-- arch-tag: 8B4EE58B-5D6D-11D9-BE92-000A957659CC

-- Add the format table.
create table format (
	format_id integer not null,
	extension varchar(4) not null,
	mime_type varchar(16) not null,
	primary key(format_id)
)
;
grant all on format to nobody
;

-- Populate some format data.
insert into format values(0, 'unk', 'image/unknown')
;
insert into format values(1, 'jpg', 'image/jpeg')
;
insert into format values(2, 'png', 'image/png')
;
insert into format values(3, 'gif', 'image/gif')
;

-- Add the format_id column to the album table
alter table album add column format_id integer
;
update album set format_id = 0
;
alter table album alter column format_id set default 0
;
alter table album add foreign key(format_id) references format(format_id)
;


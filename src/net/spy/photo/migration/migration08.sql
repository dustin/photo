-- arch-tag: 86FE8CF5-5D6D-11D9-9E9A-000A957659CC

-- General properties (photo of the unit of time, etc...)

create table properties (
	name varchar(32) not null,
	value text,
	primary key(name)
)
;
grant all on properties to nobody
;

-- Add the photo of the unit of time property
insert into properties(name, value) values('photo_of_uot', '1')
;
-- Background image property
insert into properties(name, value) values('background_img', '')
;

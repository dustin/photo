
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

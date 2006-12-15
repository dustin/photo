-- Create the place table.
create table place (
    place_id integer not null,
    name varchar(128) not null,
    lon decimal(12, 6) not null check(lon between -180 and 180),
    lat decimal(11, 6) not null check(lat between -90 and 90),
    primary key(place_id)
)
;  
grant all on place to nobody
;

-- alter the album table to include a reference to the place
alter table album add column place_id integer null
;
alter table album add constraint fk_place
	foreign key(place_id) references place(place_id)
;

-- Primary key allocation
create table primary_key (
    table_name varchar(32) not null,
    primary_key integer not null,
    incr_value integer not null
)
;
create unique index idx_pk on primary_key(table_name)
;
grant all on primary_key to nobody
;

-- Primary key for places
insert into primary_key values('place_id', 1, 1)
;

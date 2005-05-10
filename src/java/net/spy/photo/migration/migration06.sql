-- arch-tag: 8448A125-5D6D-11D9-96F3-000A957659CC

-- Galleries

create table galleries (
	gallery_id serial,
	gallery_name varchar(64) not null,
	wwwuser_id integer not null,
	ispublic boolean not null,
	ts timestamp default now(),
	primary key(gallery_id),
	foreign key(wwwuser_id) references wwwusers(id)
)
;
grant all on galleries to nobody
;
grant all on galleries_gallery_id_seq to nobody
;

-- Images stored in galleries

create table galleries_map (
	gallery_id integer not null,
	album_id integer not null,
	foreign key(gallery_id) references galleries(gallery_id),
	foreign key(album_id) references album(id)
)
;
create index galleries_mapbygal on galleries_map(gallery_id)
;
create index galleries_mapbyalbum on galleries_map(album_id)
;
grant all on galleries_map to nobody
;


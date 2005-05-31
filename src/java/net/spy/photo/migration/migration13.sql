-- arch-tag: 169B945D-43BC-487C-8B37-FEFDAF2FF79A

-- Regions

create table region (
	region_id serial,
	album_id integer not null,
	title varchar(128) not null,
	x integer not null,
	y integer not null,
	width integer not null,
	height integer not null,
	user_id integer not null,
	ts timestamp not null,
	primary key(region_id),
	foreign key(album_id) references album(id),
	foreign key(user_id) references wwwusers(id)
)
;
create index region_byimg on region(album_id)
;
grant all on region to nobody
;
grant all on region_region_id_seq to nobody
;

-- Region to keyword mapping

create table region_keywords_map (
	region_id integer not null,
	word_id integer not null,
	foreign key(region_id) references region(region_id),
	foreign key(word_id) references keywords(word_id)
)
;
create unique index region_keywords_map_byrw
	on region_keywords_map(region_id, word_id)
;
create index region_keywords_map_byr on region_keywords_map(region_id)
;
create index region_keywords_map_byw on region_keywords_map(word_id)
;
grant all on region_keywords_map to nobody
;



-- Keywords

create table keywords (
	word_id serial,
	word varchar(32) not null,
	primary key(word_id)
)
;
create unique index keywords_byword on keywords(word)
;
grant all on keywords to nobody
;
grant all on keywords_word_id_seq to nobody
;

-- keywords <-> photo mappings

create table album_keywords_map (
	album_id integer not null,
	word_id integer not null,
	foreign key(album_id) references album(id),
	foreign key(word_id) references keywords(word_id)
)
;
create unique index album_keywords_map_byu
    on album_keywords_map(album_id, word_id)
;
create index album_keywords_map_bya on album_keywords_map(album_id)
;
create index album_keywords_map_byw on album_keywords_map(word_id)
;
grant all on album_keywords_map to nobody
;


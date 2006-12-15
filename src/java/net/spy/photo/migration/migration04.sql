
-- Creating the commentary table
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
)
;
create index commentary_byphoto on commentary(photo_id)
;
create index commentary_byuser on commentary(wwwuser_id)
;
grant all on commentary to nobody
;
grant all on commentary_comment_id_seq to nobody
;

-- Creating a function to get the latest date a comment was submitted for a
-- given photo
create function latestcomment(integer) returns timestamp as
	'select max(ts) from commentary where photo_id = $1'
	language 'sql'
;

-- Creating a table to store image ranks
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
)
;
create unique index votes_byui on votes(wwwuser_id, photo_id)
;
create index votes_byphoto on votes(photo_id)
;
create index votes_byuser on votes(wwwuser_id)
;
grant all on votes to nobody
;
grant all on votes_vote_id_seq to nobody
;


-- Add the md5 column
alter table album add column md5 char(32)
;

create unique index idx_album_bymd5 on album(md5)
;

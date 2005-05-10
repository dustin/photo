-- arch-tag: 888094C3-5D6D-11D9-9AC1-000A957659CC

-- Add persistent session ID to user table
alter table wwwusers add column persess varchar(16)
;
create unique index user_bypersess on wwwusers(persess)
;

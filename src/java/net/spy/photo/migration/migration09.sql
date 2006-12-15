
-- Add persistent session ID to user table
alter table wwwusers add column persess varchar(16)
;
create unique index user_bypersess on wwwusers(persess)
;

-- arch-tag: 8DD60743-5D6D-11D9-A07A-000A957659CC

-- Drop the old keywords column
drop view log_user_ip_keywords
;
alter table album drop column keywords
;

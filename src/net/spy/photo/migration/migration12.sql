-- Drop the old keywords column
drop view log_user_ip_keywords
;
alter table album drop column keywords
;

-- arch-tag: 81802194-5D6D-11D9-85F7-000A957659CC

-- New views (for your convenience)
-- log_user_ip_agent
drop view log_user_ip_agent
;
create view log_user_ip_agent as
	select u.username, l.ts, l.remote_addr, a.user_agent,
		l.extra_info as img_size
	from wwwusers u, photo_logs l, user_agent a, log_types t
	where
		u.id = l.wwwuser_id
		and a.user_agent_id = l.user_agent
		and l.log_type = t.log_type_id
		and t.log_type ='ImgView';
;
grant select on log_user_ip_agent to nobody
;

-- log_user_ip_keywords
drop view log_user_ip_keywords
;
create view log_user_ip_keywords as
	select a.id as photo_id, u.username, l.remote_addr, a.keywords, l.ts,
		l.extra_info as img_size
	from wwwusers u, photo_logs l, album a, log_types t
	where
		u.id = l.wwwuser_id
		and a.id = l.photo_id
		and l.log_type = t.log_type_id
		and t.log_type ='ImgView';
;
grant select on log_user_ip_keywords to nobody
;

-- Login logs
create view auth_log_view as
	select
		u.username, l.remote_addr, l.ts, t.log_type
	from
		wwwusers u, photo_logs l, log_types t
	where
		u.id=l.wwwuser_id
		and l.log_type = t.log_type_id
		and (t.log_type='Login' or t.log_type='AuthFail')
	order by
		l.ts desc
;
grant select on auth_log_view to nobody
;

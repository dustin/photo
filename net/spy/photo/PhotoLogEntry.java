/*
 * Copyright (c) 1999 Dustin Sallings
 *
 * $Id: PhotoLogEntry.java,v 1.1 2002/02/25 02:38:42 dustin Exp $
 */

package net.spy.photo;

import javax.servlet.*;
import javax.servlet.http.*;

import net.spy.log.*;

/**
 * Log entries for image requests.
 */
public class PhotoLogEntry extends SpyLogEntry {

	private int photo_id=-1;
	private int wwwuser_id=-1;
	private long timestamp=-1;
	private String remote_addr=null;
	private String user_agent=null;
	private String extra_info=null;
	private String type=null;

	/**
	 * Get a new PhotoLogEntry.
	 *
	 * @param u The user ID making the request.
	 * @param p The photo ID that was requested.
	 * @param type The log type (by string name)
	 * @param request The HTTP request (to get remote addr and user agent).
	 */
	public PhotoLogEntry(int u, int p, String type,
		HttpServletRequest request) {
		super();
		this.photo_id=p;
		this.wwwuser_id=u;
		this.type=type;
		this.remote_addr=request.getRemoteAddr();
		this.user_agent=request.getHeader("User-Agent");
		this.timestamp = System.currentTimeMillis();
	}

	/**
	 * Set the extra info for this log entry.
	 */
	protected void setExtraInfo(String to) {
		this.extra_info=to;
	}

	/**
	 * String me.
	 */
	public String toString() {
		java.sql.Timestamp ts=new java.sql.Timestamp(timestamp);
		StringBuffer sb=new StringBuffer();

		sb.append("insert into photo_logs(log_type, photo_id, wwwuser_id,");
		sb.append(" remote_addr, user_agent, extra_info, ts) values(");

		sb.append("get_log_type('");
		sb.append(type);
		sb.append("'), ");

		sb.append(photo_id);
		sb.append(", ");

		sb.append(wwwuser_id);
		sb.append(", ");

		sb.append("'");
		sb.append(remote_addr);
		sb.append("', ");

		sb.append("get_agent('");
		sb.append(PhotoUtil.dbquote_str(user_agent));
		sb.append("'), ");

		if(extra_info==null) {
			sb.append("null, ");
		} else {
			sb.append("'");
			sb.append(PhotoUtil.dbquote_str(user_agent));
			sb.append("', ");
		}

		sb.append("'");
		sb.append(ts);
		sb.append("'");

		sb.append(")");

		return(sb.toString());
	}
}

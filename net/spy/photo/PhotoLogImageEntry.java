/*
 * Copyright (c) 1999 Dustin Sallings
 *
 * $Id: PhotoLogImageEntry.java,v 1.6 2002/02/24 22:50:29 dustin Exp $
 */

package net.spy.photo;

import java.util.*;
import java.text.*;
import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;

import net.spy.*;
import net.spy.log.*;

/**
 * Log entries for image requests.
 */
public class PhotoLogImageEntry extends SpyLogEntry {

	private int photo_id=-1;
	private int wwwuser_id=-1;
	private long timestamp=-1;
	private String remote_addr=null;
	private String user_agent=null;
	private PhotoDimensions size=null;

	/**
	 * Get a new PhotoLogImageEntry for a photo request.
	 *
	 * @param u The user ID making the request.
	 * @param p The photo ID that was requested.
	 * @param size The size of the image that was requested.
	 * @param request The HTTP request (to get remote addr and user agent).
	 */
	public PhotoLogImageEntry(int u, int p, PhotoDimensions size,
		HttpServletRequest request) {
		super();
		this.photo_id=p;
		this.wwwuser_id=u;
		this.remote_addr=request.getRemoteAddr();
		this.user_agent=request.getHeader("User-Agent");
		this.timestamp = System.currentTimeMillis();
		this.size=size;
	}

	/**
	 * String me.
	 */
	public String toString() {
		SimpleDateFormat f = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		java.sql.Timestamp ts=new java.sql.Timestamp(timestamp);
		StringBuffer sb=new StringBuffer();

		sb.append("insert into photo_logs(log_type, photo_id, wwwuser_id,");
		sb.append(" remote_addr, user_agent, extra_info, ts) values(");

		sb.append("get_log_type('ImgView'), ");

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

		if(size==null) {
			sb.append("null, ");
		} else {
			sb.append("'");
			sb.append(size.getWidth());
			sb.append("x");
			sb.append(size.getHeight());
			sb.append("', ");
		}

		sb.append("'");
		sb.append(ts);
		sb.append("'");

		sb.append(")");

		return(sb.toString());
	}
}

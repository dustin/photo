/*
 * Copyright (c) 1999 Dustin Sallings
 *
 * $Id: PhotoLogImageEntry.java,v 1.4 2002/02/24 21:04:29 dustin Exp $
 */

package net.spy.photo;

import java.util.*;
import java.text.*;
import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;

import net.spy.*;
import net.spy.log.*;

public class PhotoLogImageEntry extends SpyLogEntry {

	private int photo_id=null;
	private int wwwuser_id=null;
	private long timestamp;
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

	public String toString() {
		String r;
		SimpleDateFormat f = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		PhotoConfig conf = new PhotoConfig();
		java.sql.Timestamp ts=new java.sql.Timestamp(timestamp);

		r="insert into photo_log(log_type, photo_id, wwwuser_id, remote_addr, "
			+ "user_agent, extra_info, ts) values("
			+ "get_log_type('ImgView'), "
			+ photo_id + ", " + wwwuser_id + ", '" + remote_addr 
			+ "', get_agent('" + PhotoUtil.dbquote_str(user_agent) + "'),"
			+ "'" + size.getWidth() + "x" + size.getHeight() + "', "
			+ "'" + f.format(timestamp) + " "
			+ conf.get("timezone") + "', "
			+ ")";

		return(r);
	}
}

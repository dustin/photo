/*
 * Copyright (c) 1999 Dustin Sallings
 *
 * $Id: PhotoLogEntry.java,v 1.7 2002/11/03 07:33:35 dustin Exp $
 */

package net.spy.photo;

import javax.servlet.http.HttpServletRequest;

import net.spy.log.SpyLogEntry;

/**
 * Log entries for image requests.
 */
public class PhotoLogEntry extends SpyLogEntry {

	private Integer photoId=null;
	private int wwwuserId=-1;
	private long timestamp=-1;
	private String remoteAddr=null;
	private String userAgent=null;
	private String extraInfo=null;
	private String type=null;

	/**
	 * Get a new PhotoLogEntry.
	 *
	 * @param u The user ID making the request.
	 * @param p The photo ID that was requested.
	 * @param type The log type (by string name)
	 * @param request The HTTP request (to get remote addr and user agent).
	 */
	public PhotoLogEntry(int u, String type, HttpServletRequest request) {
		this(u, type, request.getRemoteAddr(), request.getHeader("User-Agent"));
	}

	/**
	 * Get a new PhotoLogEntry.
	 *
	 * @param u The user ID making the request.
	 * @param p The photo ID that was requested.
	 * @param type The log type (by string name).
	 * @param remoteAddr The address from which the request was made.
	 * @param userAgent The agent that added the image.
	 */
	public PhotoLogEntry(int u, String type, String remoteAddr,
		String userAgent) {
		super();

		this.wwwuserId=u;
		this.type=type;
		this.remoteAddr=remoteAddr;
		this.userAgent=userAgent;
		this.timestamp = System.currentTimeMillis();
	}

	/**
	 * Set the extra info for this log entry.
	 */
	protected void setExtraInfo(String to) {
		this.extraInfo=to;
	}

	/**
	 * Set the Photo ID.
	 */
	protected void setPhotoId(int to) {
		this.photoId=new Integer(to);
	}

	/**
	 * Set the Photo ID.
	 */
	protected void setPhotoId(Integer to) {
		this.photoId=to;
	}

	/**
	 * String me.
	 */
	public String toString() {
		java.sql.Timestamp ts=new java.sql.Timestamp(timestamp);
		StringBuffer sb=new StringBuffer(128);

		sb.append("insert into photo_logs(log_type, photo_id, wwwuser_id,");
		sb.append(" remote_addr, user_agent, extra_info, ts) values(");

		sb.append("get_log_type('");
		sb.append(type);
		sb.append("'), ");

		if(photoId==null) {
			sb.append("null, ");
		} else {
			sb.append(photoId);
			sb.append(", ");
		}

		sb.append(wwwuserId);
		sb.append(", ");

		sb.append("'");
		sb.append(remoteAddr);
		sb.append("', ");

		sb.append("get_agent('");
		sb.append(PhotoUtil.dbquoteStr(userAgent));
		sb.append("'), ");

		if(extraInfo==null) {
			sb.append("null, ");
		} else {
			sb.append("'");
			sb.append(PhotoUtil.dbquoteStr(extraInfo));
			sb.append("', ");
		}

		sb.append("'");
		sb.append(ts);
		sb.append("'");

		sb.append(")");

		return(sb.toString());
	}
}

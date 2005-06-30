// Copyright (c) 1999 Dustin Sallings
// arch-tag: 220C0CFE-5D6D-11D9-85A5-000A957659CC

package net.spy.photo.log;

import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;

import net.spy.db.AbstractSavable;
import net.spy.db.SaveContext;
import net.spy.db.SaveException;
import net.spy.photo.sp.InsertPhotoLog;

/**
 * Log entries for image requests.
 */
public class PhotoLogEntry extends AbstractSavable {

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
	 * @param t The log type (by string name)
	 * @param request The HTTP request (to get remote addr and user agent).
	 */
	public PhotoLogEntry(int u, String t, HttpServletRequest request) {
		this(u, t, request.getRemoteAddr(), request.getHeader("User-Agent"));
	}

	/**
	 * Get a new PhotoLogEntry.
	 *
	 * @param u The user ID making the request.
	 * @param t The log type (by string name).
	 * @param addr The address from which the request was made.
	 * @param agent The agent that added the image.
	 */
	public PhotoLogEntry(int u, String t, String addr, String agent) {
		super();

		this.wwwuserId=u;
		this.type=t;
		this.remoteAddr=addr;
		this.userAgent=agent;
		this.timestamp = System.currentTimeMillis();

		if(this.userAgent == null) {
			getLogger().warn("Got a null user agent from " + addr);
			this.userAgent = "-unknown-";
		}
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
	 * Save this log entry.
	 */
	public void save(Connection conn, SaveContext context)
		throws SaveException, SQLException {

		InsertPhotoLog ipl=new InsertPhotoLog(conn);
		ipl.setLogType(type);
		ipl.setPhotoId(photoId);
		ipl.setWwwuserId(wwwuserId);
		ipl.setRemoteAddr(remoteAddr);
		ipl.setUserAgent(userAgent);
		ipl.setExtraInfo(extraInfo);
		ipl.setTimestamp(new java.sql.Timestamp(timestamp));

		ipl.executeUpdate();

		setSaved();
	}
}

// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
// arch-tag: 2FA5E266-5D6D-11D9-A7D9-000A957659CC

package net.spy.photo.log;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import net.spy.photo.PhotoConfig;
import net.spy.photo.sp.GetViewersOfImage;

/**
 * An entry from a PhotoLogView.
 * 
 * This is ugly and will be fixed at some point.
 */
public class PhotoLogViewEntry extends Object {

	private int imageId = -1;

	private String username = null;

	private String remoteAddr = null;

	private String userAgent = null;

	private String imageSize = null;

	private Date timeStamp = null;

	/**
	 * Get an instance of PhotoLogViewEntry.
	 */
	public PhotoLogViewEntry() {
		super();
	}

	/**
	 * String me.
	 */
	@Override
	public String toString() {
		return ("{PhotoLogViewEntry id=" + imageId + ", user=" + username + "}");
	}

	// Initialize from the current row of a result set.
	private PhotoLogViewEntry(int photoId, ResultSet rs) throws Exception {
		super();

		imageId = photoId;
		username = rs.getString("username");
		remoteAddr = rs.getString("remote_addr");
		userAgent = rs.getString("user_agent");
		imageSize = rs.getString("img_size");
		timeStamp = rs.getTimestamp("ts");
	}

	/**
	 * Get a Collection of PhotoLogViewEntry objects representing the most
	 * recent viewers of the image represented by the given ID.
	 */
	public static Collection<PhotoLogViewEntry> getViewersOf(int photoId)
		throws Exception {
		Collection<PhotoLogViewEntry> al = new ArrayList<PhotoLogViewEntry>();

		GetViewersOfImage db = new GetViewersOfImage(PhotoConfig.getInstance());
		db.setImageId(photoId);

		ResultSet rs = db.executeQuery();
		while(rs.next()) {
			al.add(new PhotoLogViewEntry(photoId, rs));
		}
		rs.close();
		db.close();

		return (al);
	}

	/**
	 * Set the image ID of this log entry.
	 */
	public void setImageId(int to) {
		this.imageId = to;
	}

	/**
	 * Get the image ID of this log entry.
	 */
	public int getImageId() {
		return (imageId);
	}

	/**
	 * Set the username for this log entry.
	 */
	public void setUsername(String to) {
		this.username = to;
	}

	/**
	 * Get the username for this log entry.
	 */
	public String getUsername() {
		return (username);
	}

	/**
	 * Set the remote address used in this log entry.
	 */
	public void setRemoteAddr(String to) {
		this.remoteAddr = to;
	}

	/**
	 * Get the remote address used in this log entry.
	 */
	public String getRemoteAddr() {
		return (remoteAddr);
	}

	/**
	 * Set the user agent for this log entry.
	 */
	public void setUserAgent(String to) {
		this.userAgent = to;
	}

	/**
	 * Get the user agent for this log entry.
	 */
	public String getUserAgent() {
		return (userAgent);
	}

	/**
	 * Set the image size (dimensions) for this log entry.
	 */
	public void setImageSize(String to) {
		this.imageSize = to;
	}

	/**
	 * Get the image size (dimensions) at which this image was requested.
	 */
	public String getImageSize() {
		return (imageSize);
	}

	/**
	 * Set the timestamp of this request.
	 */
	public void setTimeStamp(Date to) {
		this.timeStamp = to;
	}

	/**
	 * Get the timestamp of this request.
	 */
	public Date getTimeStamp() {
		return (timeStamp);
	}

}

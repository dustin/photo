// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: PhotoLogViewEntry.java,v 1.4 2002/09/14 05:06:34 dustin Exp $

package net.spy.photo;

import java.sql.ResultSet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import net.spy.photo.sp.GetViewersOfImage;

/**
 * An entry from a PhotoLogView.
 *
 * This is ugly and will be fixed at some point.
 */
public class PhotoLogViewEntry extends Object {

	private int imageId=-1;
	private String username=null;
	private String remoteAddr=null;
	private String userAgent=null;
	private String imageSize=null;
	private Date timeStamp=null;

	/**
	 * Get an instance of PhotoLogViewEntry.
	 */
	public PhotoLogViewEntry() {
		super();
	}

	/**
	 * String me.
	 */
	public String toString() {
		return("{PhotoLogViewEntry id=" + imageId + ", user=" + username + "}");
	}

	// Initialize from the current row of a result set.
	private PhotoLogViewEntry(int photoId, ResultSet rs) throws Exception {
		super();

		imageId=photoId;
		username=rs.getString("username");
		remoteAddr=rs.getString("remote_addr");
		userAgent=rs.getString("user_agent");
		imageSize=rs.getString("img_size");
		timeStamp=rs.getTimestamp("ts");
	}

	/**
	 * Get a Collection of PhotoLogViewEntry objects representing the
	 * most recent viewers of the image represented by the given ID.
	 */
	public static Collection getViewersOf(int photoId) throws Exception {
		ArrayList al=new ArrayList();

		GetViewersOfImage db=new GetViewersOfImage(new PhotoConfig());
		db.setImageId(photoId);

		ResultSet rs=db.executeQuery();
		while(rs.next()) {
			al.add(new PhotoLogViewEntry(photoId, rs));
		}
		rs.close();
		db.close();

		return(al);
	}

	/**
	 * Set the image ID of this log entry.
	 */
	public void setImageId(int imageId) {
		this.imageId=imageId;
	}

	/**
	 * Get the image ID of this log entry.
	 */
	public int getImageId() {
		return(imageId);
	}

	/**
	 * Set the username for this log entry.
	 */
	public void setUsername(String username) {
		this.username=username;
	}

	/**
	 * Get the username for this log entry.
	 */
	public String getUsername() {
		return(username);
	}

	/**
	 * Set the remote address used in this log entry.
	 */
	public void setRemoteAddr(String remoteAddr) {
		this.remoteAddr=remoteAddr;
	}

	/**
	 * Get the remote address used in this log entry.
	 */
	public String getRemoteAddr() {
		return(remoteAddr);
	}

	/**
	 * Set the user agent for this log entry.
	 */
	public void setUserAgent(String userAgent) {
		this.userAgent=userAgent;
	}

	/**
	 * Get the user agent for this log entry.
	 */
	public String getUserAgent() {
		return(userAgent);
	}

	/**
	 * Set the image size (dimensions) for this log entry.
	 */
	public void setImageSize(String imageSize) {
		this.imageSize=imageSize;
	}

	/**
	 * Get the image size (dimensions) at which this image was requested.
	 */
	public String getImageSize() {
		return(imageSize);
	}

	/**
	 * Set the timestamp of this request.
	 */
	public void setTimeStamp(Date timeStamp) {
		this.timeStamp=timeStamp;
	}

	/**
	 * Get the timestamp of this request.
	 */
	public Date getTimeStamp() {
		return(timeStamp);
	}

	/**
	 * Test.
	 */
	public static void main(String args[]) throws Exception {
		for(Iterator i=getViewersOf(Integer.parseInt(args[0])).iterator();
			i.hasNext(); ) {

			System.out.println(i.next());
		}
	}

}

// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: PhotoLogViewEntry.java,v 1.1 2002/06/12 07:01:07 dustin Exp $

package net.spy.photo;

import java.util.*;
import java.sql.*;
import java.util.Date;

import net.spy.photo.sp.*;

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
	 * Get an Enumeration of PhotoLogViewEntry objects representing the
	 * most recent viewers of the image represented by the given ID.
	 */
	public static Enumeration getViewersOf(int photoId) throws Exception {
		Vector v=new Vector();

		GetViewersOfImage db=new GetViewersOfImage(new PhotoConfig());
		db.set("image_id", photoId);

		ResultSet rs=db.executeQuery();
		while(rs.next()) {
			v.addElement(new PhotoLogViewEntry(photoId, rs));
		}
		rs.close();
		db.close();

		return(v.elements());
	}

	public void setImageId(int imageId) {
		this.imageId=imageId;
	}

	public int getImageId() {
		return(imageId);
	}

	public void setUsername(String username) {
		this.username=username;
	}

	public String getUsername() {
		return(username);
	}

	public void setRemoteAddr(String remoteAddr) {
		this.remoteAddr=remoteAddr;
	}

	public String getRemoteAddr() {
		return(remoteAddr);
	}

	public void setUserAgent(String userAgent) {
		this.userAgent=userAgent;
	}

	public String getUserAgent() {
		return(userAgent);
	}

	public void setImageSize(String imageSize) {
		this.imageSize=imageSize;
	}

	public String getImageSize() {
		return(imageSize);
	}

	public void setTimeStamp(Date timeStamp) {
		this.timeStamp=timeStamp;
	}

	public Date getTimeStamp() {
		return(timeStamp);
	}

	/**
	 * Test.
	 */
	public static void main(String args[]) throws Exception {
		for(Enumeration e=getViewersOf(Integer.parseInt(args[0]));
			e.hasMoreElements(); ) {

			System.out.println(e.nextElement());
		}
	}

}

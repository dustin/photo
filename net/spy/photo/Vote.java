// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: Vote.java,v 1.2 2002/02/24 07:36:55 dustin Exp $

package net.spy.photo;

import java.sql.*;
import java.util.*;

import net.spy.*;

/**
 * Votes on photos.
 */
public class Vote extends Object
	implements java.io.Serializable, XMLAble {

	private int voteId=-1;

	private PhotoUser user=null;

	private int photoId=-1;
	private int vote=0;
	private String remoteAddr=null;
	private Timestamp timestamp=null;
	private String timestampString=null;

	/**
	 * Get an instance of Vote.
	 */
	public Vote() {
		super();
		timestamp=new java.sql.Timestamp(System.currentTimeMillis());
		timestampString=timestamp.toString();
	}

	// Get a vote from a result row
	private Vote(PhotoSecurity sec, ResultSet rs) throws Exception {
		super();

		voteId=rs.getInt("vote_id");
		photoId=rs.getInt("photo_id");
		vote=rs.getInt("vote");
		timestampString=rs.getString("ts");
		remoteAddr=rs.getString("remote_addr");
		user=sec.getUser(rs.getInt("wwwuser_id"));
	}

	/**
	 * Get an Enumeration of Vote objects for all of the comments on a
	 * given image.
	 */
	public static Enumeration getVotesForPhoto(int image_id)
		throws Exception {

		PhotoSecurity security=new PhotoSecurity();
		Vector v=new Vector();
		SpyDB db=new SpyDB(new PhotoConfig());
		PreparedStatement pst=db.prepareStatement(
			"select * from votes where photo_id=? order by ts desc");
		pst.setInt(1, image_id);
		ResultSet rs=pst.executeQuery();

		while(rs.next()) {
			v.addElement(new Vote(security, rs));
		}

		rs.close();
		pst.close();
		db.close();

		return(v.elements());
	}

	/**
	 * Save a new vote.
	 */
	public void save() throws Exception {
		if(voteId!=-1) {
			throw new Exception("You can only save *new* votes.");
		}
		if(user.getUsername().equals("guest")) {
			throw new Exception("Guest is not allowed to vote.");
		}
		SpyDB db=new SpyDB(new PhotoConfig());
		PreparedStatement pst=db.prepareStatement(
			"insert into votes(wwwuser_id,photo_id,vote,remote_addr,ts)\n"
			+ " values(?,?,?,?,?)");
		pst.setInt(1, user.getId());
		pst.setInt(2, photoId);
		pst.setInt(3, vote);
		pst.setString(4, remoteAddr);
		pst.setTimestamp(5, timestamp);

		int updated=pst.executeUpdate();
		if(updated!=1) {
			throw new Exception("No rows updated?");
		}
		pst.close();

		ResultSet rs=db.executeQuery(
			"select currval('votes_vote_id_seq')");
		if(!rs.next()) {
			System.err.println("*** Couldn't get vote ID ***");
			voteId=-2;
		} else {
			voteId=rs.getInt(1);
		}
		rs.close();
		db.close();
	}

	/**
	 * Get the vote ID of this vote record.
	 */
	public int getVoteId() {
		return(voteId);
	}

	/**
	 * Set the user who'll own this vote.
	 */
	public void setUser(PhotoUser user) {
		this.user=user;
	}

	/**
	 * Get the user who owns this vote.
	 */
	public PhotoUser getUser() {
		return(user);
	}

	/**
	 * Set the ID of the photo to which this vote belongs.
	 */
	public void setPhotoId(int photoId) {
		this.photoId=photoId;
	}

	/**
	 * Get the ID of the photo to which this vote belongs.
	 */
	public int getPhotoId() {
		return(photoId);
	}

	/**
	 * Set the actual note.
	 */
	public void setVote(int vote) {
		if(vote<0) {
			vote=0;
		} else if(vote>10) {
			vote=10;
		}
		this.vote=vote;
	}

	/**
	 * Get the note.
	 */
	public int getVote() {
		return(vote);
	}

	/**
	 * Set the remote address of the user at the time this note was added.
	 */
	public void setRemoteAddr(String remoteAddr) {
		this.remoteAddr=remoteAddr;
	}

	/**
	 * Get the remote address of the user at the time this note was added.
	 */
	public String getRemoteAddr() {
		return(remoteAddr);
	}

	/**
	 * Get the timestamp of when this entry was created.
	 */
	public String getTimestamp() {
		return(timestampString);
	}

	/**
	 * XML me.
	 */
	public String toXML() {
		StringBuffer sb=new StringBuffer();
		sb.append("<photo_vote>\n");
		sb.append("<vote_id>");
		sb.append(getVoteId());
		sb.append("</vote_id>\n");
		sb.append("<remote_addr>");
		sb.append(getRemoteAddr());
		sb.append("</remote_addr>\n");
		sb.append("<timestamp>");
		sb.append(getTimestamp());
		sb.append("</timestamp>\n");
		sb.append("<vote>");
		sb.append(getVote());
		sb.append("</vote>\n");
		sb.append(getUser().toXML());
		sb.append("</photo_vote>\n");
		return(sb.toString());
	}

	/**
	 * String me!
	 */
	public String toString() {
		StringBuffer sb=new StringBuffer();
		sb.append("Vote ");
		sb.append(getVoteId());
		sb.append(" from ");
		sb.append(getUser());
		sb.append(" on ");
		sb.append(getRemoteAddr());
		sb.append(" at ");
		sb.append(getTimestamp());
		sb.append(":  ");
		sb.append(getVote());
		return(sb.toString());
	}

	/**
	 * Testing and what not.
	 */
	public static void main(String args[]) throws Exception {
		if(args.length==2) {
			PhotoSecurity sec=new PhotoSecurity();
			PhotoUser me=sec.getUser("dustin");
			System.out.println("Got user:  " + me);

			Vote vote=new Vote();
			vote.setUser(me);
			vote.setRemoteAddr("192.168.1.139");
			vote.setPhotoId(Integer.parseInt(args[0]));
			vote.setVote(Integer.parseInt(args[1]));

			vote.save();
			System.out.println(vote);
		} else {
			int img=Integer.parseInt(args[0]);
			for(Enumeration e=Vote.getVotesForPhoto(img);
				e.hasMoreElements();) {

				Vote c=(Vote)e.nextElement();
				System.out.println(c.toXML());
				System.out.println("--");
			}
		}
	}

}

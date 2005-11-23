// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
// arch-tag: 550DB9F4-5D6D-11D9-83DA-000A957659CC

package net.spy.photo;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;

import net.spy.SpyObject;
import net.spy.db.SpyDB;

/**
 * Votes on photos.
 */
public class Vote extends SpyObject implements java.io.Serializable {

	private int voteId=-1;

	private User user=null;

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
		timestamp=new Timestamp(System.currentTimeMillis());
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
	 * Get a Collection of Vote objects for all of the comments on a
	 * given image.
	 */
	public static Collection<Vote> getVotesForPhoto(int imageId)
		throws Exception {

		PhotoSecurity security=new PhotoSecurity();
		ArrayList<Vote> al=new ArrayList<Vote>();
		SpyDB db=new SpyDB(PhotoConfig.getInstance());
		PreparedStatement pst=db.prepareStatement(
			"select * from votes where photo_id=? order by ts desc");
		pst.setInt(1, imageId);
		ResultSet rs=pst.executeQuery();

		while(rs.next()) {
			al.add(new Vote(security, rs));
		}

		rs.close();
		pst.close();
		db.close();

		return(al);
	}

	/**
	 * Save a new vote.
	 */
	public void save() throws Exception {
		if(voteId!=-1) {
			throw new Exception("You can only save *new* votes.");
		}
		if(!user.isInRole(User.AUTHENTICATED)) {
			throw new Exception("Guest is not allowed to vote.");
		}
		SpyDB db=new SpyDB(PhotoConfig.getInstance());
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
			getLogger().warn("Couldn't get vote ID");
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
	public void setUser(User to) {
		this.user=to;
	}

	/**
	 * Get the user who owns this vote.
	 */
	public User getUser() {
		return(user);
	}

	/**
	 * Set the ID of the photo to which this vote belongs.
	 */
	public void setPhotoId(int to) {
		this.photoId=to;
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
	public void setVote(int to) {
		if(to<0) {
			to=0;
		} else if(to>10) {
			to=10;
		}
		this.vote=to;
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
	public void setRemoteAddr(String to) {
		this.remoteAddr=to;
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
	 * String me!
	 */
	public String toString() {
		StringBuffer sb=new StringBuffer(64);
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
			User me=sec.getUser("dustin");
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
			for(Vote v : Vote.getVotesForPhoto(img)) {
				System.out.println(v);
				System.out.println("--");
			}
		}
	}

}

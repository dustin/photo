// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
// arch-tag: 550DB9F4-5D6D-11D9-83DA-000A957659CC

package net.spy.photo;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

import net.spy.db.AbstractSavable;
import net.spy.db.SaveContext;
import net.spy.db.SaveException;
import net.spy.photo.sp.GetGeneratedKey;
import net.spy.photo.sp.InsertVote;
import net.spy.photo.sp.UpdateVote;
import net.spy.photo.sp.VoteManipulator;

/**
 * Votes on photos.
 */
public class Vote extends AbstractSavable implements java.io.Serializable {

	public static final int MIN_VOTE = 1;
	public static final int MAX_VOTE = 5;

	private int voteId=-1;

	private User user=null;

	private int photoId=-1;
	private int vote=0;
	private String remoteAddr=null;
	private Timestamp timestamp=null;

	/**
	 * Get an instance of Vote.
	 */
	public Vote() {
		super();
		setNew(true);
	}

	// Get a vote from a result row
	public Vote(PhotoSecurity sec, ResultSet rs) throws Exception {
		super();

		voteId=rs.getInt("vote_id");
		photoId=rs.getInt("photo_id");
		vote=rs.getInt("vote");
		timestamp=rs.getTimestamp("ts");
		remoteAddr=rs.getString("remote_addr");
		user=sec.getUser(rs.getInt("wwwuser_id"));
		setNew(false);
		setModified(false);
	}

	/**
	 * Save a new vote.
	 */
	public void save(Connection conn, SaveContext ctx)
		throws SaveException, SQLException {

		if(!user.isInRole(User.AUTHENTICATED)) {
			throw new SaveException("Guest is not allowed to vote.");
		}
		VoteManipulator vm=null;
		if(isNew()) {
			vm=new InsertVote(conn);
		} else {
			vm=new UpdateVote(conn);
		}
		vm.setUserId(user.getId());
		vm.setPhotoId(photoId);
		vm.setVote(vote);
		vm.setRemoteAddr(remoteAddr);
		vm.setTs(timestamp);

		int updated=vm.executeUpdate();
		if(updated!=1) {
			throw new SaveException("No rows inserted/updated?");
		}
		vm.close();

		if(isNew()) {
			GetGeneratedKey ggk=new GetGeneratedKey(conn);
			ggk.setSeq("votes_vote_id_seq");
			ResultSet rs=ggk.executeQuery();
			if(!rs.next()) {
				throw new SaveException("Couldn't get new vote ID.");
			} else {
				voteId=rs.getInt(1);
			}
			rs.close();
			ggk.close();
		}
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
		if(to<MIN_VOTE) {
			to=MIN_VOTE;
		} else if(to>MAX_VOTE) {
			to=MAX_VOTE;
		}
		setModified(true);
		timestamp=new Timestamp(System.currentTimeMillis());
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
	public Date getTimestamp() {
		return(timestamp);
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

}

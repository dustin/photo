// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
// arch-tag: C720B970-5D6C-11D9-9568-000A957659CC

package net.spy.photo;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import net.spy.db.AbstractSavable;
import net.spy.db.SaveContext;
import net.spy.db.SaveException;
import net.spy.photo.sp.FindImagesByComments;
import net.spy.photo.sp.FindRecentComments;
import net.spy.photo.sp.GetCommentsForPhoto;
import net.spy.photo.sp.GetGeneratedKey;
import net.spy.photo.sp.InsertComment;
import net.spy.stat.Stats;
import net.spy.util.CloseUtil;

/**
 * Comments on photos.
 */
public class Comment extends AbstractSavable implements java.io.Serializable {

	private int commentId=-1;

	private User user=null;

	private int photoId=-1;
	private String note=null;
	private String remoteAddr=null;
	private Timestamp timestamp=null;

	/**
	 * Get an instance of Comment.
	 */
	public Comment() {
		super();
		timestamp=new Timestamp(System.currentTimeMillis());
		setNew(true);
		setModified(false);
	}

	// Get a comment from a result row
	private Comment(PhotoSecurity sec, ResultSet rs) throws Exception {
		super();

		commentId=rs.getInt("comment_id");
		photoId=rs.getInt("photo_id");
		note=rs.getString("note");
		timestamp=rs.getTimestamp("ts");
		remoteAddr=rs.getString("remote_addr");
		user=sec.getUser(rs.getInt("user_id"));

		setNew(false);
		setModified(false);
	}

	/**
	 * Get a Collection of Comment objects for all of the comments on a
	 * given image.
	 */
	public static Collection<Comment> getCommentsForPhoto(int imageId)
		throws Exception {

		long start=System.currentTimeMillis();
		PhotoSecurity security=Persistent.getSecurity();
		ArrayList<Comment> al=new ArrayList<Comment>();
		GetCommentsForPhoto db=
			new GetCommentsForPhoto(PhotoConfig.getInstance());
		try {
			db.setPhotoId(imageId);
			ResultSet rs=db.executeQuery();

			while(rs.next()) {
				al.add(new Comment(security, rs));
			}

			rs.close();
		} finally {
			CloseUtil.close(db);
		}

		Stats.getComputingStat("comment.image." + imageId)
			.add(System.currentTimeMillis() - start);
		return(al);
	}

	/**
	 * Get a List of GroupedComments objects that represent the
	 * comments this user can see.  Each object may represent multiple
	 * comments, but they will all refer to a single image.
	 *
	 * @see GroupedComments
	 */
	public static List<GroupedComments> getGroupedComments(User user)
		throws Exception {

		long start=System.currentTimeMillis();
		PhotoSecurity security=Persistent.getSecurity();
		ArrayList<GroupedComments> al=new ArrayList<GroupedComments>();
		FindImagesByComments db=
			new FindImagesByComments(PhotoConfig.getInstance());
		try {
			db.setUserId(user.getId());
			ResultSet rs=db.executeQuery();

			if(rs.next()) {
				GroupedComments gc=new GroupedComments(rs.getInt("photo_id"));
				gc.addComment(new Comment(security, rs));
				while(rs.next()) {
					int newid=rs.getInt("photo_id");
					if(gc.getPhotoId()!=newid) {
						// Add what we have so far
						al.add(gc);
						// Create a new one
						gc=new GroupedComments(rs.getInt("photo_id"));
					}
					// Add the current entry
					gc.addComment(new Comment(security, rs));
				}
				al.add(gc);
			}

			rs.close();
		} finally {
			CloseUtil.close(db);
		}
		Stats.getComputingStat("comment.grouplist." + user.getName())
			.add(System.currentTimeMillis() - start);
		return(al);
	}

	/**
	 * Get the recent comments for the given user.
	 * 
	 * @param u the given user
	 * @param max the maximum number of comments to return
	 * @return the comments
	 * @throws Exception if something goes wrong.
	 */
	public static List<Comment> getRecentComments(User u, int max)
		throws Exception {
		long start=System.currentTimeMillis();
		List<Comment> rv=new ArrayList<Comment>();
		FindRecentComments db=new FindRecentComments(PhotoConfig.getInstance());
		try {
			db.setUserId(u.getId());
			db.setMaxRows(max);
			ResultSet rs=db.executeQuery();
			while(rs.next()) {
				rv.add(new Comment(Persistent.getSecurity(), rs));
			}
			rs.close();
			assert rv.size() <= max : "Got too many results";
		} finally {
			CloseUtil.close(db);
		}
		Stats.getComputingStat("comment.list." + u.getName())
			.add(System.currentTimeMillis() - start);
		return rv;
	}

	// Savable implementation

	/**
	 * Save a new comment.
	 */
	public void save(Connection conn, SaveContext context)
		throws SaveException, SQLException {
		// Basic checks
		if(!isNew()) {
			throw new SaveException("You can only save *new* comments.");
		}
		if(!user.getRoles().contains(User.AUTHENTICATED)) {
			throw new SaveException("Guest is not allowed to comment.");
		}

		InsertComment db=new InsertComment(conn);
		db.setUserId(user.getId());
		db.setPhotoId(photoId);
		db.setComment(note);
		db.setRemoteAddr(remoteAddr);
		db.setTimestamp(timestamp);

		int updated=db.executeUpdate();
		if(updated!=1) {
			throw new SaveException("No rows updated?");
		}
		db.close();

		GetGeneratedKey ggk=new GetGeneratedKey(conn);
		ggk.setSeq("commentary_comment_id_seq");
		ResultSet rs=ggk.executeQuery();
		if(!rs.next()) {
			getLogger().warn("*** Couldn't get comment ID ***");
			commentId=-2;
		} else {
			commentId=rs.getInt(1);
		}
		rs.close();
		ggk.close();
	}

	// End savable implementation

	/**
	 * Get the comment ID of this comment record.
	 */
	public int getCommentId() {
		return(commentId);
	}

	/**
	 * Set the user who'll own this comment.
	 */
	public void setUser(User to) {
		this.user=to;
	}

	/**
	 * Get the user who owns this comment.
	 */
	public User getUser() {
		return(user);
	}

	/**
	 * Set the ID of the photo to which this comment belongs.
	 */
	public void setPhotoId(int to) {
		this.photoId=to;
	}

	/**
	 * Get the ID of the photo to which this comment belongs.
	 */
	public int getPhotoId() {
		return(photoId);
	}

	/**
	 * Set the actual note.
	 */
	public void setNote(String to) {
		this.note=to;
	}

	/**
	 * Get the note.
	 */
	public String getNote() {
		return(note);
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
	@Override
	public String toString() {
		StringBuilder sb=new StringBuilder(128);
		sb.append("Comment ");
		sb.append(getCommentId());
		sb.append(" from ");
		sb.append(getUser());
		sb.append(" on ");
		sb.append(getRemoteAddr());
		sb.append(" at ");
		sb.append(getTimestamp());
		sb.append(":\n");
		sb.append(getNote());
		return(sb.toString());
	}

}

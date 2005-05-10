// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
// arch-tag: C720B970-5D6C-11D9-9568-000A957659CC

package net.spy.photo;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.sql.Connection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.spy.db.SpyDB;
import net.spy.db.AbstractSavable;
import net.spy.db.SaveException;
import net.spy.db.SaveContext;

import net.spy.photo.sp.FindImagesByComments;
import net.spy.photo.sp.GetCommentsForPhoto;
import net.spy.photo.sp.InsertComment;
import net.spy.photo.sp.GetGeneratedKey;

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
	private String timestampString=null;

	/**
	 * Get an instance of Comment.
	 */
	public Comment() {
		super();
		timestamp=new Timestamp(System.currentTimeMillis());
		timestampString=timestamp.toString();
		setNew(true);
		setModified(false);
	}

	// Get a comment from a result row
	private Comment(PhotoSecurity sec, ResultSet rs) throws Exception {
		super();

		commentId=rs.getInt("comment_id");
		photoId=rs.getInt("photo_id");
		note=rs.getString("note");
		timestampString=rs.getString("ts");
		remoteAddr=rs.getString("remote_addr");
		user=sec.getUser(rs.getInt("user_id"));

		setNew(false);
		setModified(false);
	}

	/**
	 * Get a Collection of Comment objects for all of the comments on a
	 * given image.
	 */
	public static Collection getCommentsForPhoto(int imageId)
		throws Exception {

		PhotoSecurity security=new PhotoSecurity();
		ArrayList al=new ArrayList();
		GetCommentsForPhoto db=
			new GetCommentsForPhoto(PhotoConfig.getInstance());
		db.setPhotoId(imageId);
		ResultSet rs=db.executeQuery();

		while(rs.next()) {
			al.add(new Comment(security, rs));
		}

		rs.close();
		db.close();

		return(al);
	}

	/**
	 * Get a List of GroupedComments objects that represent the
	 * comments this user can see.  Each object may represent multiple
	 * comments, but they will all refer to a single image.
	 *
	 * @see GroupedComments
	 */
	public static List getAllComments(User user) throws Exception {

		PhotoSecurity security=new PhotoSecurity();
		ArrayList al=new ArrayList();
		FindImagesByComments db=
			new FindImagesByComments(PhotoConfig.getInstance());
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
		db.close();

		return(al);
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
		if(user.getName().equals("guest")) {
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

		setSaved();
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
	public void setUser(User user) {
		this.user=user;
		setModified(true);
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
	public void setPhotoId(int photoId) {
		this.photoId=photoId;
		setModified(true);
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
	public void setNote(String note) {
		this.note=note;
		setModified(true);
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
	public void setRemoteAddr(String remoteAddr) {
		this.remoteAddr=remoteAddr;
		setModified(true);
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
		StringBuffer sb=new StringBuffer(128);
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

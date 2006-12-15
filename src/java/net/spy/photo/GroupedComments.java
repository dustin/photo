// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>

package net.spy.photo;


/**
 * Represents a group of comments for an individual image.
 */
public class GroupedComments extends Cursor<Comment> {

	private int imageId=-1;
	private boolean hasMore=false;
	// This may be editable at some point, it configures the maximum number
	// of comments that can be in this group.
	private int maxComments=5;

	/**
	 * Get an instance of GroupedComments.
	 */
	public GroupedComments(int id) {
		super();
		this.imageId=id;
	}

	/**
	 * Get the ID of this image.
	 */
	public int getPhotoId() {
		return(imageId);
	}

	/**
	 * Return true if there are more comments available.
	 */
	public boolean getMoreAvailable() {
		return(hasMore);
	}

	/**
	 * Add a comment.  If there are already enough comments, mark it as
	 * having more.
	 */
	public void addComment(Comment comment) {
		if(imageId != comment.getPhotoId()) {
			throw new Error(comment.getPhotoId() + " != " + imageId);
		}

		if(getSize() < maxComments) {
			add(comment);
		} else if(getSize() == maxComments) {
			hasMore=true;
		}
	}

}

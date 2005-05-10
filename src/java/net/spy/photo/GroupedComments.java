// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
// arch-tag: D62649EF-5D6C-11D9-9D14-000A957659CC

package net.spy.photo;

import java.io.Serializable;

import java.util.ArrayList;

/**
 * Represents a group of comments for an individual image.
 */
public class GroupedComments extends ArrayList implements Serializable {

	private int imageId=-1;
	private boolean hasMore=false;
	// This may be editable at some point, it configures the maximum number
	// of comments that can be in this group.
	private int maxComments=5;

	/**
	 * Get an instance of GroupedComments.
	 */
	public GroupedComments(int imageId) {
		super();
		this.imageId=imageId;
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
	public boolean hasMore() {
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

		if(size() < maxComments) {
			add(comment);
		} else if(size() == maxComments) {
			hasMore=true;
		}
	}

}

// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: GroupedComments.java,v 1.2 2002/06/23 02:10:40 dustin Exp $

package net.spy.photo;

import java.util.Vector;
import java.util.Enumeration;
import java.io.Serializable;

/**
 * Represents a group of comments for an individual image.
 */
public class GroupedComments extends Vector implements Serializable {

	private int imageId=-1;
	private boolean hasMore=false;
	// This may be editable at some point, it configures the maximum number
	// of comments that can be in this group.
	private int maxComments=5;

	/**
	 * Get an instance of GroupedComments.
	 */
	public GroupedComments(int image_id) {
		super();
		this.imageId=image_id;
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
			addElement(comment);
		} else if(size() == maxComments) {
			hasMore=true;
		}
	}

}

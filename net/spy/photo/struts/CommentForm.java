// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: CommentForm.java,v 1.2 2002/07/10 03:38:09 dustin Exp $

package net.spy.photo.struts;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

/**
 * Form used when a user submits a comment.
 */
public class CommentForm extends ActionForm {

	private String comment=null;
	private String imageId=null;

	/**
	 * Get an instance of CommentForm.
	 */
	public CommentForm() {
		super();
	}

	public void setComment(String comment) {
		this.comment=comment;
	}

	public String getComment() {
		return(comment);
	}

	public void setImageId(String imageId) {
		this.imageId=imageId;
	}

	public String getImageId() {
		return(imageId);
	}

	/**
	 * Reset all properties.
	 */
	public void reset(ActionMapping mapping, HttpServletRequest request) {
		comment=null;
		imageId=null;
	}

	/**
	 * Validate the input.
	 */
	public ActionErrors validate(ActionMapping mapping,
		HttpServletRequest request) {

		ActionErrors errors = new ActionErrors();

		if(comment==null || comment.length() < 1) {
			errors.add("comment", new ActionError("error.comment.comment"));
		}

		if(imageId==null || imageId.length() < 1) {
			errors.add("imageId", new ActionError("error.comment.imageId"));
		}

		try {
			Integer.parseInt(imageId);
		} catch(NumberFormatException nfe) {
			errors.add("imageId", new ActionError("error.comment.nfe"));
		}

		return(errors);
	}

}

// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: ImageDataTag.java,v 1.5 2002/07/10 03:38:09 dustin Exp $

package net.spy.photo.taglib;

import javax.servlet.jsp.JspException;

import net.spy.photo.Persistent;
import net.spy.photo.PhotoImageData;
import net.spy.photo.PhotoSearchResults;
import net.spy.photo.PhotoSessionData;

/**
 * Taglib to get image data.
 */
public class ImageDataTag extends PhotoTag {

	private String imageId=null;
	private String searchId=null;

	/**
	 * Get an instance of ImageDataTag.
	 */
	public ImageDataTag() {
		super();
		// Reset everything.
		release();
	}

	/**
	 * Set the absolute image ID.
	 */
	public void setImageId(String imageId) {
		this.imageId=imageId;
	}

	/**
	 * Set the relative search offset.
	 */
	public void setSearchId(String searchId) {
		this.searchId=searchId;
	}

	// Get the results by image ID.
	private PhotoImageData doDisplayByID(int id) throws JspException {
		PhotoSessionData sessionData=getSessionData();

		try {
			// Check access
			Persistent.getSecurity().checkAccess(sessionData.getUser(), id);
		} catch(Exception e) {
			throw new JspException("You're not allowed to see " + id);
		}
		// Get the data
		PhotoImageData r=null;
		// Fetch up the image
		try {
			r=PhotoImageData.getData(id, sessionData.getOptimalDimensions());
		} catch(Exception e) {
			e.printStackTrace();
			throw new JspException("Couldn't find image:  " + e);
		}

		return(r);
	}

	// get the results by search ID.
	private PhotoImageData doDisplayBySearchId(int id) throws JspException {

		PhotoSessionData sessionData=getSessionData();
		PhotoSearchResults results=sessionData.getResults();
		if(results==null) {
			throw new JspException("No results in session.");
		}

		PhotoImageData r = (PhotoImageData)results.get(id);
		if(r==null) {
			throw new JspException("No matching result found in session.");
		}

		return(r);
	}

	/**
	 * Set the vars and all that.
	 */
	public int doStartTag() throws JspException {
		if(imageId!=null && searchId!=null) {
			throw new JspException(
				"Must choose between a search ID and an image ID "
					+ "(imageId=" + imageId + ", searchId=" + searchId + ")");
		}

		PhotoImageData r=null;

		if(imageId!=null) {
			r=doDisplayByID(Integer.parseInt(imageId));
		} else if(searchId!=null) {
			r=doDisplayBySearchId(Integer.parseInt(searchId));
		} else {
			throw new JspException(
				"Must choose between a search ID and an image ID.");
		}

		if(r==null) {
			throw new JspException("OK, this isn't supposed to happen.");
		}

		// Save it.
		pageContext.setAttribute("image", r);

		return(EVAL_BODY_INCLUDE);
	}

	/**
	 * Release state.
	 */
	public void release() {
		imageId=null;
		searchId=null;
	}

	/**
	 * Unset the vars and all that.
	 */
	public int doEndTag() throws JspException {
		// Remove it.
		pageContext.removeAttribute("image");
		return(EVAL_PAGE);
	}

}

// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: ImageLink.java,v 1.13 2003/08/11 05:49:54 dustin Exp $

package net.spy.photo.taglib;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;

import javax.servlet.jsp.JspException;

import net.spy.photo.PhotoUtil;
import net.spy.photo.PhotoImageData;
import net.spy.photo.PhotoImageDataImpl;
import net.spy.photo.PhotoDimensions;
import net.spy.photo.PhotoDimScaler;
import net.spy.photo.PhotoImageHelper;
import net.spy.photo.PhotoSessionData;

/**
 * Taglib to link to an image.
 */
public class ImageLink extends PhotoTag {

	private int id=0;
	private boolean showThumbnail=false;
	private boolean showOptimal=false;
	private String width=null;
	private String height=null;
	private boolean scale=false;
	private String altText=null;

	/**
	 * Get an instance of ImageLink.
	 */
	public ImageLink() {
		super();
		release();
	}

	/**
	 * Set the id of the image to which we want to link.
	 */
	public void setId(String to) {
		id=Integer.parseInt(to);
	}

	/**
	 * Set the id of the image to which we want to link.
	 */
	public void setId(int to) {
		id=to;
	}

	/**
	 * If ``true'' show a thumbnail.
	 */
	public void setShowThumbnail(String to) {
		this.showThumbnail=Boolean.valueOf(to).booleanValue();
	}

	/**
	 * If ``true'' show the optimal size.
	 */
	public void setShowOptimal(String to) {
		this.showOptimal=Boolean.valueOf(to).booleanValue();
	}

	/**
	 * If ``true'' scale the image to the given width and height.
	 */
	public void setScale(String to) {
		this.scale=Boolean.valueOf(to).booleanValue();
	}

	/**
	 * Set the image width.
	 */
	public void setWidth(String width) {
		this.width=width;
	}

	/**
	 * Set the image height.
	 */
	public void setHeight(String height) {
		this.height=height;
	}

	/**
	 * Set the alt text for the thumbnail (if provided).
	 */
	public void setAlt(String altText) {
		this.altText=altText;
	}

	/**
	 * Start link.
	 */
	public int doStartTag() throws JspException {

		StringBuffer href=new StringBuffer();
		StringBuffer url=new StringBuffer();

		href.append("<img src=\"");

		HttpServletRequest req=(HttpServletRequest)pageContext.getRequest();
		url.append(PhotoUtil.getRelativeUri(req, "/PhotoServlet/"));

		// Get the PhotoSessionData so we can figure out the width and height
		HttpSession session=req.getSession(false);
		PhotoSessionData sessionData=
			(PhotoSessionData)session.getAttribute(PhotoSessionData.SES_ATTR);
		if(sessionData==null) {
			throw new JspException("No photoSession in session.");
		}

		if(showOptimal) {
			scale=true;
			try {
				PhotoImageData pid=PhotoImageDataImpl.getData(id);
				PhotoDimensions optdims=sessionData.getOptimalDimensions();
				PhotoDimensions newDims=PhotoDimScaler.scaleTo(
					pid.getDimensions(), optdims);

				width=String.valueOf(newDims.getWidth());
				height=String.valueOf(newDims.getHeight());
			} catch(Exception e) {
				JspException e2=new JspException("Couldn't get image");
				e2.initCause(e);
				throw e2;
			}
		}
		
		url.append(id);
		url.append(".jpg?id=");
		url.append(id);
		if(scale && (width!=null) && (height!=null)) {
			url.append("&amp;scale=");
			url.append(width);
			url.append("x");
			url.append(height);
		}

		if(showThumbnail) {
			url.append("&amp;thumbnail=1");
		}

		// Get the response for rewriting
		HttpServletResponse res=(HttpServletResponse)pageContext.getResponse();
		href.append(res.encodeURL(url.toString()));

		// Finish the src attribute.
		href.append("\"");

		String tmpAlt=altText;
		if(altText==null) {
			tmpAlt="image " + id;
		}
		href.append(" alt=\"");
		href.append(tmpAlt);
		href.append("\"");

		// if no width or height was provided, figure out out
		if( (width == null) && (height == null) ) {
			try {
				if(showThumbnail) {
					PhotoImageHelper ph=new PhotoImageHelper(id);
					PhotoDimensions size=ph.getThumbnailSize();
					width="" + size.getWidth();
					height="" + size.getHeight();
				}
			} catch(Exception e) {
				// Just print the stack trace, leave the width and height
				// blank.
				e.printStackTrace();
			}
		}

		if(width!=null) {
			href.append(" width=\"");
			href.append(width);
			href.append("\"");
		}
		if(height!=null) {
			href.append(" height=\"");
			href.append(height);
			href.append("\"");
		}

		href.append("/>");

		try {
			pageContext.getOut().write(href.toString());
		} catch(Exception e) {
			e.printStackTrace();
			throw new JspException("Error sending output:  " + e);
		}

		return(EVAL_BODY_INCLUDE);
	}

	/**
	 * Reset all values.
	 */
	public void release() {
		id=0;
		showThumbnail=false;
		width=null;
		height=null;
		scale=false;
		altText=null;
	}

}

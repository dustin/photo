// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: ImageLink.java,v 1.3 2002/05/15 08:28:06 dustin Exp $

package net.spy.photo.taglib;

import javax.servlet.*;
import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;

/**
 * Taglib to link to an image.
 */
public class ImageLink extends PhotoTag {

	private int id=0;
	private boolean showThumbnail=false;
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
		Boolean b=new Boolean(to);
		this.showThumbnail=b.booleanValue();
	}

	/**
	 * If ``true'' scale the image to the given width and height.
	 */
	public void setScale(String to) {
		Boolean b=new Boolean(to);
		this.scale=b.booleanValue();
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

		StringBuffer sb=new StringBuffer();
		sb.append("<img src=\"PhotoServlet?func=getimage&photo_id=");
		sb.append(id);
		if(scale && (width!=null) && (height!=null)) {
			sb.append("&scale=");
			sb.append(width);
			sb.append("x");
			sb.append(height);
		}

		if(showThumbnail) {
			sb.append("&thumbnail=1");
		}

		// Finish the src attribute.
		sb.append("\"");

		sb.append(" border=\"0\"");
		if(altText!=null) {
			sb.append(" alt=\"");
			sb.append(altText);
			sb.append("\"");
		}

		if(width!=null) {
			sb.append(" width=\"");
			sb.append(width);
			sb.append("\"");
		}
		if(height!=null) {
			sb.append(" height=\"");
			sb.append(height);
			sb.append("\"");
		}

		sb.append("/>");

		try {
			pageContext.getOut().write(sb.toString());
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

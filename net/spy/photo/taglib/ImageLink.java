// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: ImageLink.java,v 1.6 2002/07/01 18:03:19 dustin Exp $

package net.spy.photo.taglib;

import javax.servlet.*;
import javax.servlet.http.*;
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

		StringBuffer href=new StringBuffer();
		StringBuffer url=new StringBuffer();

		href.append("<img src=\"");
		
		url.append("PhotoServlet/");
		url.append(id);
		url.append(".jpg?id=");
		url.append(id);
		if(scale && (width!=null) && (height!=null)) {
			url.append("&scale=");
			url.append(width);
			url.append("x");
			url.append(height);
		}

		if(showThumbnail) {
			url.append("&thumbnail=1");
		}

		// Get the response for rewriting
		HttpServletResponse res=(HttpServletResponse)pageContext.getResponse();
		href.append(res.encodeURL(url.toString()));

		// Finish the src attribute.
		href.append("\"");

		href.append(" border=\"0\"");
		if(altText!=null) {
			href.append(" alt=\"");
			href.append(altText);
			href.append("\"");
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

// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: DisplayLink.java,v 1.1 2002/05/15 08:26:15 dustin Exp $

package net.spy.photo.taglib;

import javax.servlet.*;
import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;

/**
 * Taglib to link to an image.
 */
public class DisplayLink extends PhotoTag {

	private int id=0;
	private boolean showThumbnail=false;
	private String altText=null;

	/**
	 * Get an instance of ImageLink.
	 */
	public DisplayLink() {
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
	 * Set the alt text for the thumbnail (if provided).
	 */
	public void setAlt(String altText) {
		this.altText=altText;
	}

	/**
	 * Start link.
	 */
	public int doStartTag() throws JspException {

		System.out.println("Creating a link to " + id);

		StringBuffer sb=new StringBuffer();
		sb.append("<a href=\"display.jsp?id=");
		sb.append(id);
		sb.append("\">");
		if(showThumbnail) {
			sb.append("<img src=\"PhotoServlet?func=getimage&photo_id=");
			sb.append(id);
			sb.append("&thumbnail=1\" border=\"0\"");
			if(altText!=null) {
				sb.append(" alt=\"");
				sb.append(altText);
				sb.append("\"");
			}
			sb.append("></img>");
		}

		try {
			pageContext.getOut().write(sb.toString());
		} catch(Exception e) {
			e.printStackTrace();
			throw new JspException("Error sending output:  " + e);
		}

		return(EVAL_BODY_INCLUDE);
	}

	/**
	 * End link.
	 */
	public int doEndTag() throws JspException {
		try {
			pageContext.getOut().write("</a>");
		} catch(Exception e) {
			e.printStackTrace();
			throw new JspException("Error sending output:  " + e);
		}

		return(EVAL_PAGE);
	}

	/**
	 * Reset all values.
	 */
	public void release() {
		id=0;
		showThumbnail=false;
		altText=null;
	}

}

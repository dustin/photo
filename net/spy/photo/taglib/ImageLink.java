// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: ImageLink.java,v 1.1 2002/05/15 04:27:53 dustin Exp $

package net.spy.photo.taglib;

import javax.servlet.*;
import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;

/**
 * Taglib to link to an image.
 */
public class ImageLink extends TagSupport {

	private int id=0;
	private boolean showThumbnail=false;

	/**
	 * Get an instance of ImageLink.
	 */
	public ImageLink() {
		super();
	}

	/**
	 * Set the id of the image to which we want to link.
	 */
	public void setId(String to) {
		id=Integer.parseInt(to);
	}

	public void setShowThumbnail(String to) {
		Boolean b=new Boolean(to);
		this.showThumbnail=b.booleanValue();
	}

	/**
	 * Start link.
	 */
	public int doStartTag() throws JspException {

		StringBuffer sb=new StringBuffer();
		sb.append("<a href=\"PhotoServlet?func=display&id=");
		sb.append(id);
		sb.append("\">");
		if(showThumbnail) {
			sb.append("<img src=\"PhotoServlet?func=getimage&photo_id=");
			sb.append(id);
			sb.append("&thumbnail=1\" border=\"0\"></img>");
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

}

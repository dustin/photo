// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: Link.java,v 1.1 2002/06/10 18:35:10 dustin Exp $

package net.spy.photo.taglib;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;

import net.spy.photo.PhotoUtil;

/**
 * Taglib to link to an image.
 */
public class Link extends PhotoTag {

	private String url=null;

	/**
	 * Get an instance of ImageLink.
	 */
	public Link() {
		super();
		release();
	}

	/**
	 * Set the relative URL to which to link.
	 */
	public void setUrl(String url) {
		this.url=url;
	}

	/**
	 * Get the relative URL to which to link.
	 */
	public String getUrl() {
		return(url);
	}

	/**
	 * Start link.
	 */
	public int doStartTag() throws JspException {

		StringBuffer sb=new StringBuffer();
		sb.append("<a href=\"");

		HttpServletRequest req=(HttpServletRequest)pageContext.getRequest();
		sb.append(PhotoUtil.getRelativeUri(req, url));
		sb.append("\">");

		try {
			pageContext.getOut().write(sb.toString());
		} catch(Exception e) {
			e.printStackTrace();
			throw new JspException("Error sending output:  " + e);
		}

		return(EVAL_BODY_INCLUDE);
	}

	/**
	 * End the link.
	 */
	public int doAfterBody() throws JspException {
		try {
			pageContext.getOut().write("</a>");
		} catch(Exception e) {
			e.printStackTrace();
			throw new JspException("Error sending output:  " + e);
		}

		return(SKIP_BODY);
	}

	/**
	 * Reset all values.
	 */
	public void release() {
		url=null;
	}

}

// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: Stylesheet.java,v 1.1 2002/06/20 04:34:17 dustin Exp $

package net.spy.photo.taglib;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;

import net.spy.photo.PhotoUtil;

/**
 * Taglib to provide a stylesheet.
 */
public class Stylesheet extends PhotoTag {

	private String url=null;

	/**
	 * Get an instance of Stylesheet.
	 */
	public Stylesheet() {
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
		sb.append("<link href=\"");
		HttpServletRequest req=(HttpServletRequest)pageContext.getRequest();
		sb.append(PhotoUtil.getRelativeUri(req, url));
		sb.append("\" rel=\"stylesheet\">");

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
		url=null;
	}

}

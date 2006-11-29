// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
// arch-tag: E84F4B93-9A01-42D0-8E13-EDFB07F2E6C1

package net.spy.photo.taglib;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;

import net.spy.photo.PhotoUtil;

/**
 * Taglib to provide a javascript reference.
 */
public class Javascript extends PhotoTag {

	private String url=null;

	/**
	 * Get an instance of Javascript.
	 */
	public Javascript() {
		super();
		release();
	}

	/**
	 * Set the relative URL to which to link.
	 */
	public void setUrl(String to) {
		this.url=to;
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
	@Override
	public int doStartTag() throws JspException {

		StringBuffer sb=new StringBuffer();
		sb.append("<script type=\"text/javascript\" src=\"");
		HttpServletRequest req=(HttpServletRequest)pageContext.getRequest();
		sb.append(PhotoUtil.getRelativeUri(req, url));
		sb.append("\"></script>");

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
	@Override
	public void release() {
		url=null;
	}

}

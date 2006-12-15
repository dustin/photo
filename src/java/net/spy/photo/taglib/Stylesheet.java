// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>

package net.spy.photo.taglib;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;

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
		sb.append("<link href=\"");
		HttpServletRequest req=(HttpServletRequest)pageContext.getRequest();
		sb.append(PhotoUtil.getRelativeUri(req, url));
		sb.append("\" rel=\"stylesheet\" />");

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

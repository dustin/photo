// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: Link.java,v 1.4 2002/06/30 05:09:10 dustin Exp $

package net.spy.photo.taglib;

import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;

import net.spy.photo.PhotoUtil;

/**
 * Taglib to provide relative links within the webapp.
 */
public class Link extends PhotoTag {

	private String url=null;
	private String message=null;

	/**
	 * Get an instance of Link.
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
	 * Get the message to display.
	 */
	public String getMessage() {
		return(message);
	}

	/**
	 * Set the message to display.
	 */
	public void setMessage(String message) {
		this.message=message;
	}

	/**
	 * Start link.
	 */
	public int doStartTag() throws JspException {

		boolean usedMessage=false;

		StringBuffer sb=new StringBuffer();
		sb.append("<a href=\"");

		// Get the request and response
		HttpServletRequest req=(HttpServletRequest)pageContext.getRequest();
		HttpServletResponse res=(HttpServletResponse)pageContext.getResponse();

		// Get the URL
		String relurl=PhotoUtil.getRelativeUri(req, url);
		// Add it (rewritten).
		sb.append(res.encodeURL(relurl));
		sb.append("\">");

		String content=null;

		if(message!=null) {
			try {
				ResourceBundle rb=getResourceBundle();
				content=rb.getString(message);
			} catch(MissingResourceException mre) {
				mre.printStackTrace();
				content="[missing resource:  " + message + "]";
			}
			usedMessage=true;
		}

		try {
			JspWriter out=pageContext.getOut();

			out.write(sb.toString());

			// If we used an internal message, we can close the a tag.
			if(usedMessage) {
				out.write(content);
				out.write("</a>");
			}
		} catch(Exception e) {
			e.printStackTrace();
			throw new JspException("Error sending output:  " + e);
		}

		int rv=EVAL_BODY_INCLUDE;
		// If we used an internal message, skip the body.
		if(usedMessage) {
			rv=SKIP_BODY;
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

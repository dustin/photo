// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: Link.java,v 1.7 2002/07/10 03:38:09 dustin Exp $

package net.spy.photo.taglib;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

import net.spy.photo.PhotoUtil;

/**
 * Taglib to provide relative links within the webapp.
 */
public class Link extends PhotoTag {

	private String url=null;
	private String message=null;
	private String id=null;

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
	 * Set the image ID to include in this link.
	 */
	public void setId(String id) {
		this.id=id;
	}

	/**
	 * Get the image ID to include in this link.
	 */
	public String getId() {
		return(id);
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

		StringBuffer tmpUrl=new StringBuffer(url);
		if(id!=null) {
			if(url.indexOf("?")>=0) {
				tmpUrl.append("&");
			} else {
				tmpUrl.append("?");
			}
			tmpUrl.append("id=");
			tmpUrl.append(id);
		}

		// Get the URL
		String relurl=PhotoUtil.getRelativeUri(req, tmpUrl.toString());
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

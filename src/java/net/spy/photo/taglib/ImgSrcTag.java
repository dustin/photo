// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
// arch-tag: 4E1B2C0E-5D6E-11D9-9B46-000A957659CC

package net.spy.photo.taglib;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;

import net.spy.photo.PhotoUtil;

/**
 * Taglib to link to an image.
 */
public class ImgSrcTag extends PhotoTag {

	private String url=null;
	private String styleClass=null;
	private String width=null;
	private String height=null;
	private String alt=null;

	/**
	 * Get an instance of ImageLink.
	 */
	public ImgSrcTag() {
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
	 * Set an HTML class setting for this image.
	 */
	public void setBorder(String styleClass) {
		this.styleClass=styleClass;
	}

	/**
	 * Set the width of this image.
	 */
	public void setWidth(String width) {
		this.width=width;
	}

	/**
	 * Set the height of this image.
	 */
	public void setHeight(String height) {
		this.height=height;
	}

	/**
	 * Set the alt text for this image link.
	 */
	public void setAlt(String alt) {
		this.alt=alt;
	}

	/**
	 * Start link.
	 */
	public int doStartTag() throws JspException {

		StringBuffer sb=new StringBuffer();
		sb.append("<img src=\"");

		HttpServletRequest req=(HttpServletRequest)pageContext.getRequest();
		sb.append(PhotoUtil.getRelativeUri(req, url));
		sb.append("\"");

		if(styleClass!=null) {
			sb.append(" class=\"");
			sb.append(styleClass);
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

		String tmpAlt=alt;
		if(alt==null) {
			tmpAlt="";
		}
		sb.append(" alt=\"");
		sb.append(tmpAlt);
		sb.append("\""); 

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
		url=null;
	}

}

// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: DisplayLink.java,v 1.3 2002/05/16 18:35:24 dustin Exp $

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
    private String width=null;
    private String height=null;

	/**
	 * Get an instance of ImageLink.
	 */
	public DisplayLink() {
		super();
		release();
	}

    /**
     * Set the width for the img src HTML tag.
     */
    public void setWidth(String width) {
        this.width=width;
    }

    /**
     * Set the height for the img src HTML tag.
     */
    public void setHeight(String height) {
        this.height=height;
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

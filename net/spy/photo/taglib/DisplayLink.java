// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: DisplayLink.java,v 1.7 2002/05/23 16:24:49 dustin Exp $

package net.spy.photo.taglib;

import javax.servlet.*;
import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;

import net.spy.photo.*;

/**
 * Taglib to link to an image.
 */
public class DisplayLink extends PhotoTag {

	private int id=-1;
	private int searchId=-1;
	private boolean showThumbnail=false;
	private String altText=null;
	private String width=null;
	private String height=null;

	private String relative=null;

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
	 * Set the search ID.
	 */
	public void setSearchId(String to) {
		if(to!=null) {
			searchId=Integer.parseInt(to);
		}
	}

	/**
	 * Set the search ID.
	 */
	public void setSearchId(int to) {
		searchId=to;
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
	 * Set a relative type for this link.
	 *
	 * Relative links to search offsets may be of type ``prev'' or
	 * ``next.''
	 */
	public void setRelative(String relative) {
		this.relative=relative;
	}

	/**
	 * Get the relative type.
	 */
	public String getRelative() {
		return(relative);
	}

	/**
	 * Start link.
	 */
	public int doStartTag() throws JspException {

        // If this is true at the bottom, process the link.
        boolean process=true;

		StringBuffer sb=new StringBuffer();

		sb.append("<a href=\"display.jsp?");
		// Figure out whether to link to the search ID or the real ID.
		if(searchId>=0) {
			sb.append("search_id=");

			PhotoSessionData sessionData=getSessionData();
			PhotoSearchResults results=sessionData.getResults();
			if(results==null) {
				throw new JspException("No search results found.");
			}

			// Figure out the maximum number of results.
			int maxIndex=results.nResults();

			// Since this is a search offset, figure out whether it's
			// relative the the actual value or not.
			if(relative==null) {
				// Nothing
			} else if(relative.equals("prev")) {
				if(searchId == 0) {
					process=false; // Don't process
				} else {
					searchId--;
				}
			} else if(relative.equals("next")) {
				if(searchId>=maxIndex) {
					process=false; // Don't process
				} else {
					searchId++;
				}
			} else {
				throw new JspException("Invalid relative type:  " + relative);
			}

			sb.append(searchId);

		} else {
			// This is meant to be a relative link, but didn't get a search ID.
			if(relative!=null) {
				process=false;
			}
			// real ID.
			sb.append("id=");
			sb.append(id);
		}
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
			if(process) {
				pageContext.getOut().write(sb.toString());
			}
		} catch(Exception e) {
			e.printStackTrace();
			throw new JspException("Error sending output:  " + e);
		}

		// Figure out the return value
		int rv=process?EVAL_BODY_INCLUDE:SKIP_BODY;

		return(rv);
	}

	/**
	 * End link.
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
		id=0;
		showThumbnail=false;
		altText=null;
	}

}

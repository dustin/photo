// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: DisplayLink.java,v 1.11 2002/07/04 06:57:59 dustin Exp $

package net.spy.photo.taglib;

import javax.servlet.*;
import javax.servlet.http.*;
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

		// This variable is the full HREF that will be rendered
		StringBuffer href=new StringBuffer();
		// This is just the URL inside the HREF.
		StringBuffer url=new StringBuffer("display.jsp?");

		href.append("<a href=\"");
		// Figure out whether to link to the search ID or the real ID.
		if(searchId>=0) {
			url.append("search_id=");

			PhotoSessionData sessionData=getSessionData();
			PhotoSearchResults results=sessionData.getResults();
			if(results==null) {
				throw new JspException("No search results found.");
			}

			// Figure out the maximum number of results.
			int maxIndex=results.size();

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

			url.append(searchId);

		} else {
			// This is meant to be a relative link, but didn't get a search ID.
			if(relative!=null) {
				process=false;
			}
			// real ID.
			url.append("id=");
			url.append(id);
		}

		// Need the response to rewrite the URL
		HttpServletResponse res=(HttpServletResponse)pageContext.getResponse();
		href.append(res.encodeURL(url.toString()));

		href.append("\">");
		if(showThumbnail) {
			href.append("<img src=\"");

			StringBuffer tmpurl=new StringBuffer();
			tmpurl.append("PhotoServlet?id=");
			tmpurl.append(id);
			tmpurl.append("&thumbnail=1");

			// Encode the photo display page
			href.append(res.encodeURL(tmpurl.toString()));

			href.append("\" border=\"0\"");
			if(altText!=null) {
				href.append(" alt=\"");
				href.append(altText);
				href.append("\"");
			}
			if(width!=null) {
				href.append(" width=\"");
				href.append(width);
				href.append("\"");
			}
			if(height!=null) {
				href.append(" height=\"");
				href.append(height);
				href.append("\"");
			}
			href.append("></img>");
		}

		try {
			if(process) {
				pageContext.getOut().write(href.toString());
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

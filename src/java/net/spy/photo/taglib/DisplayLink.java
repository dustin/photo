// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>

package net.spy.photo.taglib;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;

import net.spy.photo.PhotoDimensions;
import net.spy.photo.PhotoImageDataFactory;
import net.spy.photo.PhotoImageHelper;
import net.spy.photo.PhotoSessionData;
import net.spy.photo.PhotoUtil;

/**
 * Taglib to link to an image.
 */
public class DisplayLink extends PhotoTag {

	private int id=-1;
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
	public void setWidth(String to) {
		this.width=to;
	}

	/**
	 * Set the height for the img src HTML tag.
	 */
	public void setHeight(String to) {
		this.height=to;
	}

	/**
	 * Set the id of the image to which we want to link.
	 */
	@Override
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
		this.showThumbnail=Boolean.valueOf(to).booleanValue();
	}

	/**
	 * Set the alt text for the thumbnail (if provided).
	 */
	public void setAlt(String to) {
		this.altText=to;
	}

	/**
	 * Set a relative type for this link.
	 *
	 * Relative links to search offsets may be of type ``prev'' or
	 * ``next.''
	 */
	public void setRelative(String to) {
		this.relative=to;
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
	@Override
	public int doStartTag() throws JspException {

        // If this is true at the bottom, process the link.
        boolean process=true;

		// This variable is the full HREF that will be rendered
		StringBuilder href=new StringBuilder();

		HttpServletRequest req=(HttpServletRequest)pageContext.getRequest();
		// This is just the URL inside the HREF.
		StringBuffer url=new StringBuffer();
		url.append(PhotoUtil.getRelativeUri(req, "/display.do?"));

		href.append("<a href=\"");
		PhotoSessionData sesData=getSessionData();
		int searchPos=sesData.getResultPos(id);

		if(relative==null) {
			// real ID.
		} else if(relative.equals("prev")) {
			id=sesData.getResultIdByPosition(searchPos-1);
			if(id == -1) {
				process=false; // Don't process
			}
		} else if(relative.equals("next")) {
			id=sesData.getResultIdByPosition(searchPos+1);
			if(id == -1) {
				process=false; // Don't process
			}
		} else {
			throw new JspException("Invalid relative type:  " + relative);
		}

		url.append("id=");
		url.append(id);

		// Need the response to rewrite the URL
		HttpServletResponse res=(HttpServletResponse)pageContext.getResponse();
		href.append(res.encodeURL(url.toString()));

		href.append("\">");
		if(showThumbnail) {
			href.append("<img src=\"");

			StringBuffer tmpurl=new StringBuffer();
			tmpurl.append(PhotoUtil.getRelativeUri(req, "/PhotoServlet?id="));
			tmpurl.append(id);
			tmpurl.append("&amp;thumbnail=1\"");

			// Encode the photo display page
			href.append(res.encodeURL(tmpurl.toString()));

			String tmpAlt=altText;
			if(altText==null) {
				tmpAlt="image " + id;
			}
			href.append(" alt=\"");
			href.append(tmpAlt);
			href.append("\"");

			String w=null;
			String h=null;

			if( (width == null) && (height == null) ) {
				try {
					PhotoImageHelper ph=PhotoImageHelper.getInstance();
					PhotoDimensions size=ph.getThumbnailSize(
							PhotoImageDataFactory.getInstance().getObject(id));
					w="" + size.getWidth();
					h="" + size.getHeight();
				} catch(Exception e) {
					// Just print the stack trace, leave the width and height
					// blank.
					e.printStackTrace();
				}
			} else {
				w=width;
				h=height;
			}

			if(w!=null) {
				href.append(" width=\"");
				href.append(w);
				href.append("\"");
			}
			if(h!=null) {
				href.append(" height=\"");
				href.append(h);
				href.append("\"");
			}
			href.append(" />");
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
	@Override
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
	@Override
	public void release() {
		id=0;
		showThumbnail=false;
		altText=null;
		width=null;
		height=null;
	}

}

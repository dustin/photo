// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: RefreshTag.java,v 1.1 2003/04/25 06:32:23 dustin Exp $

package net.spy.photo.taglib;

import javax.servlet.http.HttpServletRequest;

import javax.servlet.jsp.JspException;

import net.spy.photo.PhotoUtil;
import net.spy.photo.RefreshBean;

/**
 * Taglib to provide a refresh if one is requested.
 */
public class RefreshTag extends PhotoTag {

	/** 
	 * The attribute containing the refresh information.
	 */
	public static final String REFRESH_BEAN="refreshBean";

	/**
	 * Get an instance of RefreshTag.
	 */
	public RefreshTag() {
		super();
	}

	/**
	 * Start link.
	 */
	public int doStartTag() throws JspException {

		// Try to find the refresh bean
		RefreshBean refreshBean=
			(RefreshBean)pageContext.findAttribute(REFRESH_BEAN);
		// If you found one, jump on it!
		if(refreshBean != null) {
			StringBuffer sb=new StringBuffer();
			sb.append("<meta http-equiv=\"refresh\" content=\"");
			sb.append(refreshBean.getDelay());
			if(refreshBean.getLocation()!=null) {
				sb.append("; ");
				HttpServletRequest req=
					(HttpServletRequest)pageContext.getRequest();
				sb.append(PhotoUtil.getRelativeUri(
					req, refreshBean.getLocation()));
			}
			sb.append("\">");

			try {
				pageContext.getOut().write(sb.toString());
			} catch(Exception e) {
				e.printStackTrace();
				throw new JspException("Error sending output:  " + e);
			}
		}

		return(EVAL_BODY_INCLUDE);
	}

}

// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: SessionInfo.java,v 1.1 2002/06/10 19:01:16 dustin Exp $

package net.spy.photo.taglib;

import java.text.*;
import java.sql.*;

import javax.servlet.*;
import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;

import net.spy.photo.*;

/**
 * Meta information about users logged into the site.
 */
public class SessionInfo extends PhotoTag {

	/**
	 * Get an instance of SessionInfo.
	 */
	public SessionInfo() {
		super();
	}

	/**
	 * Get the meta info and shove it into the webpage.
	 */
	public int doStartTag() throws JspException {
		try {
			// get the number formatter.
			NumberFormat nf=NumberFormat.getNumberInstance();

			String out="There are currently "
				+ nf.format(SessionWatcher.totalSessions())
				+ " people browsing, "
				+ nf.format(SessionWatcher.getSessionCountByUser("guest"))
				+ " are guest.";
			if(out!=null) {
				pageContext.getOut().write(out);
			}
		} catch(Exception e) {
			e.printStackTrace();
			throw new JspException("Error sending output.");
		}

		return(EVAL_BODY_INCLUDE);
	}

}

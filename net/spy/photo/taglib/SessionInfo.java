// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: SessionInfo.java,v 1.3 2002/07/10 03:38:09 dustin Exp $

package net.spy.photo.taglib;

import java.text.MessageFormat;

import javax.servlet.jsp.JspException;

import net.spy.photo.SessionWatcher;

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

            Object args[]={
                new Double(SessionWatcher.totalSessions()),
                new Double(SessionWatcher.getSessionCountByUser("guest"))
                };

			String out=MessageFormat.format(
				"There {0,choice,0#are|1#is|2#are} currently "
					+ "{0,number,integer} "
					+ "{0,choice,0#people|1#person|2#people} "
					+ "browsing, {1,number,integer} "
					+ "{1,choice,0#are|1#is|2#are} guest.",
                args);

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

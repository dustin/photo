// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: PhotoTag.java,v 1.1 2002/05/15 08:26:16 dustin Exp $

package net.spy.photo.taglib;

import javax.servlet.*;
import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;

import net.spy.photo.*;

/**
 * Superclass for all taglibs.
 */
public abstract class PhotoTag extends TagSupport {

	private PhotoSessionData sessionData=null;

	/**
	 * Get an instance of PhotoTag.
	 */
	public PhotoTag() {
		super();
	}

	/**
	 * Get the session data object.
	 */
	protected PhotoSessionData getSessionData() throws JspException {
		if(sessionData==null) {
			sessionData=(PhotoSessionData)
				pageContext.getAttribute("sessionData",
					PageContext.REQUEST_SCOPE);

			if(sessionData==null) {
				throw new JspException("sessionData not in request scope.");
			}
		}

		return(sessionData);
	}

}

// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>

package net.spy.photo.taglib;

import java.util.Locale;
import java.util.ResourceBundle;

import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import net.spy.photo.PhotoSessionData;

/**
 * Superclass for all taglibs.
 */
public abstract class PhotoTag extends BodyTagSupport {

	private PhotoSessionData sessionData=null;

	/**
	 * Get the session data object.
	 */
	protected PhotoSessionData getSessionData() throws JspException {
		if(sessionData==null) {
			HttpSession session=pageContext.getSession();
			sessionData=(PhotoSessionData)
				session.getAttribute(PhotoSessionData.SES_ATTR);

			if(sessionData==null) {
				throw new JspException("photoSession not in session.");
			}
		}

		return(sessionData);
	}

	/**
	 * Get the application resource bundle.
	 */
	protected ResourceBundle getResourceBundle() {
		Locale l=pageContext.getRequest().getLocale();
		ResourceBundle rb=ResourceBundle.getBundle(
			"net.spy.photo.photoresources", l);
		return(rb);
	}

}

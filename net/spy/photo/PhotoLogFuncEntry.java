/*
 * Copyright (c) 1999 Dustin Sallings
 *
 * $Id: PhotoLogFuncEntry.java,v 1.1 2002/02/25 02:46:41 dustin Exp $
 */

package net.spy.photo;

import javax.servlet.*;
import javax.servlet.http.*;

import net.spy.log.*;

/**
 * Log entries for image requests.
 */
public class PhotoLogFuncEntry extends PhotoLogEntry {

	/**
	 * Get a new PhotoLogFuncEntry for a servlet request.
	 *
	 * @param u The user ID making the request.
	 * @param p The photo ID that was requested.
	 * @param func The function that was called.
	 * @param request The HTTP request (to get remote addr and user agent).
	 */
	public PhotoLogFuncEntry(int u, String func,
		HttpServletRequest request) {
		super(u, "Request", request);

		setExtraInfo(func);
	}

}

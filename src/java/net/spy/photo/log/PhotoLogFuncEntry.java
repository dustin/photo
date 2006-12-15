// Copyright (c) 1999 Dustin Sallings

package net.spy.photo.log;

import javax.servlet.http.HttpServletRequest;

/**
 * Log entries for image requests.
 */
public class PhotoLogFuncEntry extends PhotoLogEntry {

	/**
	 * Get a new PhotoLogFuncEntry for a servlet request.
	 *
	 * @param u The user ID making the request.
	 * @param func The function that was called.
	 * @param request The HTTP request (to get remote addr and user agent).
	 */
	public PhotoLogFuncEntry(int u, String func,
		HttpServletRequest request) {
		super(u, "Request", request);

		setExtraInfo(func);
	}

}

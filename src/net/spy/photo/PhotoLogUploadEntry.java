/*
 * Copyright (c) 1999 Dustin Sallings
 *
 * $Id: PhotoLogUploadEntry.java,v 1.3 2002/07/10 03:38:08 dustin Exp $
 */

package net.spy.photo;

import javax.servlet.http.HttpServletRequest;

/**
 * Log entries for image requests.
 */
public class PhotoLogUploadEntry extends PhotoLogEntry {

	/**
	 * Get a new PhotoLogUploadEntry for a photo request.
	 *
	 * @param u The user ID making the request.
	 * @param p The photo ID that was requested.
	 * @param request The HTTP request (to get remote addr and user agent).
	 */
	public PhotoLogUploadEntry(int u, int p, HttpServletRequest request) {
		super(u, "Upload", request);

		setPhotoId(p);
	}

	/**
	 * Get a new PhotoLogUploadEntry for a photo request.
	 *
	 * @param u The user ID making the request.
	 * @param p The photo ID that was requested.
	 * @param remoteAddr the IP address of the remote end
	 * @param userAgent the remote user agent
	 */
	public PhotoLogUploadEntry(int u, int p, String remoteAddr,
		String userAgent) {
		super(u, "Upload", remoteAddr, userAgent);

		setPhotoId(p);
	}

}

// Copyright (c) 1999 Dustin Sallings
// arch-tag: 2DF8CAA6-5D6D-11D9-95E9-000A957659CC

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

// Copyright (c) 1999 Dustin Sallings
// arch-tag: 2A43E778-5D6D-11D9-A5AB-000A957659CC

package net.spy.photo.log;

import javax.servlet.http.HttpServletRequest;

import net.spy.photo.PhotoDimensions;

/**
 * Log entries for image requests.
 */
public class PhotoLogImageEntry extends PhotoLogEntry {

	/**
	 * Get a new PhotoLogImageEntry for a photo request.
	 *
	 * @param u The user ID making the request.
	 * @param p The photo ID that was requested.
	 * @param size The size of the image that was requested.
	 * @param request The HTTP request (to get remote addr and user agent).
	 */
	public PhotoLogImageEntry(int u, int p, PhotoDimensions size,
		HttpServletRequest request) {
		super(u, "ImgView", request);

		setPhotoId(p);
		if(size!=null) {
			setExtraInfo(size.getWidth() + "x" + size.getHeight());
		}
	}

}

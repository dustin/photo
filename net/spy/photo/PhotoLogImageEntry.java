/*
 * Copyright (c) 1999 Dustin Sallings
 *
 * $Id: PhotoLogImageEntry.java,v 1.8 2002/02/25 02:46:41 dustin Exp $
 */

package net.spy.photo;

import javax.servlet.*;
import javax.servlet.http.*;

import net.spy.log.*;

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

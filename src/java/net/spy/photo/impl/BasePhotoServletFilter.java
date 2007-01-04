package net.spy.photo.impl;

import javax.servlet.http.HttpServletRequest;

import net.spy.SpyObject;
import net.spy.photo.Persistent;
import net.spy.photo.PhotoConfig;
import net.spy.photo.PhotoDimensions;
import net.spy.photo.PhotoServletFilter;
import net.spy.photo.User;
import net.spy.photo.log.PhotoLogImageEntry;

/**
 * Base class for photoservlet filters.
 */
public abstract class BasePhotoServletFilter extends SpyObject
	implements PhotoServletFilter {

	protected void logAccess(User u, int imgId, PhotoDimensions dims,
			HttpServletRequest req) {
		// Log it
		Persistent.getPipeline().addTransaction(
				new PhotoLogImageEntry(u.getId(), imgId, dims, req),
				PhotoConfig.getInstance());
	}
}

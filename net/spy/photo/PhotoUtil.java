/*
 * Copyright (c) 1999 Dustin Sallings
 *
 * $Id: PhotoUtil.java,v 1.15 2002/07/10 04:00:17 dustin Exp $
 */

package net.spy.photo;

import java.text.SimpleDateFormat;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import net.spy.SpyDB;
import net.spy.SpyUtil;

/**
 * Utilities.
 */
public class PhotoUtil extends Object { 

	/**
	 * Simple splitter.
	 */
	public static String[] split(String on, String input) {
		return(SpyUtil.split(on, input));
	}

	/**
	 * Get a quoted string for doing DB stuff where PreparedStatements
	 * won't fit.
	 */
	public static String dbquoteStr(String thing) {
		return(SpyDB.dbquote_str(thing));
	}

	/**
	 * Get today's date as a string.
	 */
	public static String getToday() {
		Date ts=new Date();
		SimpleDateFormat f=new SimpleDateFormat("MM/dd/yyyy");
		return(f.format(ts));
	}

	/**
	 * Get the default ID used for inheriting category access.
	 */
	public static int getDefaultId() {
		int ret=-1;

		try {
			PhotoUser u=getDefaultUser();
			ret=u.getId();
		} catch(Exception e) {
			ret=-1;
		}

		return(ret);
	}

	/**
	 * Get the default user.
	 */
	public static PhotoUser getDefaultUser() throws Exception {
		PhotoSecurity s=new PhotoSecurity();
		PhotoConfig conf = new PhotoConfig();
		String user=conf.get("default_user", "guest");
		PhotoUser u=s.getUser(user);
		return(u);
	}

	/**
	 * Get a relative URI inside the current webapp.
	 *
	 * @param req a servlet request within this webapp
	 * @param uri A uri (beginning with /) within the webapp.
	 */
	public static String getRelativeUri(HttpServletRequest req, String uri) {
		StringBuffer sb=new StringBuffer();
		sb.append(req.getContextPath());

		if(!uri.startsWith("/")) {
			throw new IllegalArgumentException(
				"uri parameter must begin with a slash.");
		}
		sb.append(uri);

		return(sb.toString());
	}
}

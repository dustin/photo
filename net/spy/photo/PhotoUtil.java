/*
 * Copyright (c) 1999 Dustin Sallings
 *
 * $Id: PhotoUtil.java,v 1.12 2002/06/23 02:10:40 dustin Exp $
 */

package net.spy.photo;

import java.lang.*;
import java.util.*;
import java.text.*;
import java.io.File;

import javax.servlet.http.HttpServletRequest;

import net.spy.*;

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
	public static String dbquote_str(String thing) {
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
	 * Hash hashing.
	 */
	public static int myHash(Hashtable in) {
		int i=0;
		for(Enumeration e=in.elements(); e.hasMoreElements(); ) {
			i+=e.nextElement().hashCode();
		}
		return(i);
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

/*
 * Copyright (c) 1999 Dustin Sallings
 *
 * $Id: PhotoUtil.java,v 1.19 2003/07/23 04:29:26 dustin Exp $
 */

package net.spy.photo;

import java.util.Collection;
import java.util.ArrayList;
import java.util.Iterator;
import java.text.SimpleDateFormat;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import net.spy.db.SpyDB;
import net.spy.util.SpyUtil;
import net.spy.jwebkit.RequestUtil;

/**
 * Utilities.
 */
public class PhotoUtil extends Object {

	private static final Collection dateFormats=initDateFormats();

	private static Collection initDateFormats() {
		ArrayList rv=new ArrayList();

		SimpleDateFormat sdf=new SimpleDateFormat("MM/dd/yyyy");
		sdf.setLenient(false);
		rv.add(sdf);

		sdf=new SimpleDateFormat("MM-dd-yyyy");
		sdf.setLenient(false);
		rv.add(sdf);

		sdf=new SimpleDateFormat("yyyy-MM-dd");
		sdf.setLenient(false);
		rv.add(sdf);

		sdf=new SimpleDateFormat("yyyy/MM/dd");
		sdf.setLenient(false);
		rv.add(sdf);

		return(rv);
	}

	/** 
	 * Parse a date in one of the known formats.
	 * @param s a string to parse
	 * @return the date, or null if the date could not be parsed
	 */
	public static Date parseDate(String s) {
		Date rv=null;
		if(s != null && s.length() > 0) {
			for(Iterator i=dateFormats.iterator(); rv==null && i.hasNext(); ) {
				SimpleDateFormat sdf=(SimpleDateFormat)i.next();

				try {
					rv=sdf.parse(s);
				} catch(Exception e) {
					// Ignored, try the next one
				}
			}
		}
		return(rv);
	}

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
		return(SpyDB.dbquoteStr(thing));
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
		PhotoUser u=s.getDefaultUser();
		return(u);
	}

	/**
	 * Get a relative URI inside the current webapp.
	 *
	 * @param req a servlet request within this webapp
	 * @param uri A uri (beginning with /) within the webapp.
	 */
	public static String getRelativeUri(HttpServletRequest req, String uri) {
		return(RequestUtil.getRelativeUri(req, uri));
	}
}

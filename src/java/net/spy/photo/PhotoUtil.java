// Copyright (c) 1999 Dustin Sallings
// arch-tag: 490DF968-5D6D-11D9-8C7B-000A957659CC

package net.spy.photo;

import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import net.spy.db.SpyDB;
import net.spy.jwebkit.RequestUtil;
import net.spy.util.SpyUtil;

/**
 * Utilities.
 */
public class PhotoUtil extends Object {

	private static final Collection<SimpleDateFormat> dateFormats
		=initDateFormats();

	private static Collection<SimpleDateFormat> initDateFormats() {
		ArrayList<SimpleDateFormat> rv=new ArrayList<SimpleDateFormat>();

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
			for(SimpleDateFormat sdf : dateFormats) {
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
			User u=getDefaultUser();
			ret=u.getId();
		} catch(Exception e) {
			ret=-1;
		}

		return(ret);
	}

	/**
	 * Get the default user.
	 */
	public static User getDefaultUser() throws Exception {
		PhotoSecurity s=new PhotoSecurity();
		User u=s.getDefaultUser();
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

	/**
	 * Get a new ID for a photo.
	 */
	public static int getNewIdForSeq(String seq) throws PhotoException {
		int rv=0;
		try {
			SpyDB db=new SpyDB(PhotoConfig.getInstance());
			ResultSet rs=db.executeQuery("select nextval('" + seq  + "')");
			if(!rs.next()) {
				throw new PhotoException("No result for new ID from " + seq);
			}
			rv=rs.getInt(1);
			if(rs.next()) {
				throw new PhotoException("Too many results for new ID from "
					+ seq);
			}
			rs.close();
			db.close();
		} catch(PhotoException e) {
			throw e;
		} catch(Exception e) {
			throw new PhotoException("Error getting ID from " + seq, e);
		}
		return(rv);
	}

}

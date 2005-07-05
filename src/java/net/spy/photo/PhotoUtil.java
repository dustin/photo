// Copyright (c) 1999 Dustin Sallings
// arch-tag: 490DF968-5D6D-11D9-8C7B-000A957659CC

package net.spy.photo;

import java.sql.ResultSet;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import net.spy.db.SpyDB;
import net.spy.jwebkit.RequestUtil;
import net.spy.photo.impl.PhotoDimensionsImpl;
import net.spy.photo.impl.PhotoRegionImpl;

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
					// Special case for people who try to enter pictures without
					// specifying the date properly.
					Calendar cal=Calendar.getInstance();
					cal.setTime(rv);
					int year=cal.get(Calendar.YEAR);
					if(year < 100) {
						rv=null;
					}
				} catch(Exception e) {
					// Ignored, try the next one
				}
			}
		}
		return(rv);
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

	/**
	 * Get the scale factor that will be used to scale the first dimensions to
	 * fit as tightly as possible within the constraints of the second
	 * dimensions.
	 *
	 * @param from the dimensions to be scaled
	 * @param to the constraints
	 * @return the scaling factor that will scale the dims to the constraints
	 */
	public static float getScaleFactor(PhotoDimensions from,
		PhotoDimensions to) {
	
		float fromw=from.getWidth();
		float fromh=from.getHeight();
		float tow=to.getWidth();
		float toh=to.getHeight();
	
		float scaleFactor=tow/fromw;
		if(fromh * scaleFactor > toh) {
			scaleFactor=toh/fromh;
		}
	
		// Assertions
		if( (int)((fromw * scaleFactor)) > tow
			|| (int)((fromh * scaleFactor)) > toh) {
	
			throw new RuntimeException(
				"Results can't be outside of the input box:  "
				+ from + " -> " + to + " yielded " + scaleFactor + " for "
				+ (fromw * scaleFactor) + "x" + (fromh * scaleFactor));
		}
		// End assertions
	
		return(scaleFactor);
	}

	/**
	 * Scale a set of PhotoDimensions by a specific factor.
	 *
	 * @param from the source PhotoDimensions
	 * @param factor the factor by which to scale
	 * @return the scaled PhotoDimensions
	 */
	public static PhotoDimensions scaleBy(PhotoDimensions from, float factor) {
		PhotoDimensions rv=new PhotoDimensionsImpl(
			(int)(from.getWidth() * factor),
			(int)(from.getHeight() * factor));
		return(rv);
	}

	/**
	 * Scale a dimension to another dimension.
	 * This will only scale down, not up.
	 *
	 * @param from the dimensions of the source
	 * @param to the maximum dimensions to scale to
	 * @return the largest dimensions of from that fit within to
	 */
	public static PhotoDimensions scaleTo(PhotoDimensions from,
		PhotoDimensions to) {

		PhotoDimensions rv=from;

		// This prevents us from scaling down.  We only scale if the
		// constraints are smaller than the from dimensions
		if(to.getWidth() < from.getWidth()
			|| to.getHeight() < from.getHeight()) {

			rv=scaleBy(from, getScaleFactor(from, to));
		}

		return(rv);
	}

	/**
	 * Determine whether the first PhotoDimensions instance is smaller than the
	 * second in area.
	 *
	 * @return true if the area of a is greater than the area of b.
	 */
	public static boolean smallerThan(PhotoDimensions a, PhotoDimensions b) {
		int areaa=a.getWidth() * a.getHeight();
		int areab=b.getWidth() * b.getHeight();
		return(areaa < areab);
	}

	/**
	 * Scale a region by a factor.
	 *
	 * @param rin the region
	 * @param factor the factor
	 * @return the region applied to a specific scaling factor
	 */
	public static PhotoRegion scaleRegion(PhotoRegion rin, float factor) {
		PhotoRegion rv=new PhotoRegionImpl(
			(int)(rin.getX() * factor),
			(int)(rin.getY() * factor),
			(int)(rin.getWidth() * factor),
			(int)(rin.getHeight() * factor));
		return(rv);
	}

}

// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: PhotoImageData.java,v 1.2 2002/03/05 04:32:23 dustin Exp $

package net.spy.photo;

import java.sql.*;

import net.spy.*;
import net.spy.cache.*;

/**
 * This class represents, and retreives all useful data for a given image.
 */
public class PhotoImageData extends Object {

	private int id=-1;
	private String keywords=null;
	private String descr=null;
	private String catName=null;
	private int catId=-1;
	private int size=-1;
	private int width=-1;
	private int height=-1;

	private PhotoUser addedBy=null;

	// I need this to be a string until I can sort out my JDBC driver
	// problem.
	private String timestamp=null;
	private String taken=null;

	private PhotoImageData() {
		super();
	}

	private void initFromResultSet(ResultSet rs) throws Exception {
		id=rs.getInt("id");
		catId=rs.getInt("catid");
		size=rs.getInt("size");
		width=rs.getInt("width");
		height=rs.getInt("height");
		keywords=rs.getString("keywords");
		descr=rs.getString("descr");
		catName=rs.getString("catname");
		timestamp=rs.getString("ts");
		taken=rs.getString("taken");

		PhotoSecurity ps=new PhotoSecurity();
		addedBy=ps.getUser(rs.getInt("addedby"));
	}

	/**
	 * Get the data for the given ID.
	 */
	public static PhotoImageData getData(int id) throws Exception {
		SpyCache sc=new SpyCache();

		String key="photo_idata_" + id;
		PhotoImageData rv=(PhotoImageData)sc.get(key);
		if(rv==null) {
			rv=getDataFromDB(id);

			// Cache it for an hour.
			sc.store(key, rv, 3600000);
		}

		return(rv);
	}

	// Go get the live data.
	private static PhotoImageData getDataFromDB(int id) throws Exception {
		PhotoImageData rv=new PhotoImageData();

		String query="select a.descr, a.keywords, a.cat as catid, a.taken, "
			+ "a.size, a.addedby, a.width, a.height, a.ts, a.id, "
			+ "c.name as catname\n"
			+ "from album a, cat c\n"
			+ "where a.cat=c.id and a.id=?";

		SpyDB db=new SpyDB(new PhotoConfig());
		PreparedStatement pst=db.prepareStatement(query);
		pst.setInt(1, id);

		ResultSet rs=pst.executeQuery();
		if(rs.next()) {
			rv.initFromResultSet(rs);
		} else {
			rs.close();
			db.close();
			throw new Exception("No image found for " + id);
		}

		rs.close();
		db.close();

		return(rv);
	}

	/**
	 * Get the keywords for this photo.
	 */
	public String getKeywords() {
		return(keywords);
	}

	/**
	 * Get the description of this photo.
	 */
	public String getDescr() {
		return(descr);
	}

	/**
	 * Get the category ID of this photo.
	 */
	public int getCatId() {
		return(catId);
	}

	/**
	 * Get the size (in bytes) of this photo.
	 */
	public int getSize() {
		return(size);
	}

	/**
	 * Get the width of this photo.
	 */
	public int getWidth() {
		return(width);
	}

	/**
	 * Get the height of this photo.
	 */
	public int getHeight() {
		return(height);
	}

	/**
	 * Get the PhotoUser object representing the user who added this photo.
	 */
	public PhotoUser getAddedBy() {
		return(addedBy);
	}

	/**
	 * Get the timestamp this photo was added (currently as a String until
	 * I sort out my JDBC driver problem).
	 */
	public String getTimestamp() {
		return(timestamp);
	}

	/**
	 * Get the name of the category containing this image.
	 */
	public String getCatName() {
		return(catName);
	}

	/**
	 * Get the date this photo was taken.
	 */
	public String getTaken() {
		return(taken);
	}

	/**
	 * Get the ID of this image.
	 */
	public int getId() {
		return(id);
	}

}

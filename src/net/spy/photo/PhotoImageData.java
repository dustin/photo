// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: PhotoImageData.java,v 1.13 2003/07/26 08:38:27 dustin Exp $

package net.spy.photo;

import java.io.Serializable;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import net.spy.SpyDB;

import net.spy.cache.SpyCache;

import net.spy.photo.sp.GetPhotoInfo;

/**
 * This class represents, and retreives all useful data for a given image.
 */
public class PhotoImageData extends Object implements Serializable, Cloneable {

	private int id=-1;
	private String keywords=null;
	private String descr=null;
	private String catName=null;
	private int catId=-1;
	private int size=-1;

	// Search id.
	private int searchId=-1;

	// Dimensions of the full size image.
	private PhotoDimensions dimensions=null;

	// Dimensions of the thumbnail of this image.
	private PhotoDimensions tnDims=null;
	// This variable is used for scaling the image for display.
	private PhotoDimensions maxDims=null;
	private PhotoDimensions scaledDims=null;

	private PhotoUser addedBy=null;

	// I need this to be a string until I can sort out my JDBC driver problem.
	private String timestamp=null;
	private String taken=null;

	private PhotoImageData() {
		super();
	}

	/**
	 * String me.
	 */
	public String toString() {
		return("{PhotoImageData id=" + id + " - " + dimensions + "}");
	}

	private void initFromResultSet(ResultSet rs) throws Exception {
		id=rs.getInt("id");
		catId=rs.getInt("catid");
		size=rs.getInt("size");
		int width=rs.getInt("width");
		if(rs.wasNull()) {
			width=-1;
		}
		int height=rs.getInt("height");
		if(rs.wasNull()) {
			height=-1;
		}
		keywords=rs.getString("keywords");
		descr=rs.getString("descr");
		catName=rs.getString("catname");
		timestamp=rs.getString("ts");
		taken=rs.getString("taken");

		// Look up the user
		addedBy=Persistent.getSecurity().getUser(rs.getInt("addedby"));

		// Get the dimensions object if a valid width and height came back
		// from the DB.
		if(width>=0 && height>=0) {
			dimensions=new PhotoDimensionsImpl(width, height);
		}

		// Calculate the thumbnail size.
		calculateThumbnail();
	}

	/**
	 * Set the maximum dimensions for display of this image and calculate
	 * the scaled dimensions to stay within the maximum dimensions and
	 * maintain the proper aspect ratio.
	 */
	public void setMaxDims(PhotoDimensions maxDims) {
		this.maxDims=maxDims;
		calculateScaled();
	}

	/**
	 * True if the given object is a PhotoImageData object representing the
	 * same image.
	 */
	public boolean equals(Object o) {
		boolean rv=false;

		if(o instanceof PhotoImageData) {
			PhotoImageData pid=(PhotoImageData)o;

			if(id == pid.getId()) {
				rv=true;
			}
		}

		return(rv);
	}

	/** 
	 * Get the hash code for this object.
	 * 
	 * @return the id
	 */
	public int hashCode() {
		return(id);
	}

	// Calculate the thumbnail size
	private void calculateThumbnail() {
		if(dimensions!=null) {
			// get the optimal thumbnail dimensions
			PhotoConfig conf=new PhotoConfig();
			PhotoDimensions tdim=new PhotoDimensionsImpl(
				conf.get("thumbnail_size"));

			// get a scaler
			PhotoDimScaler pds=new PhotoDimScaler(dimensions);
			// Scale it down
			tnDims=pds.scaleTo(tdim);
		}
	}

	// Calculate the scaled dimensions for image viewing
	private void calculateScaled() {
		if(dimensions!=null && maxDims!=null) {
			// Get the scaler and scale them down.
			PhotoDimScaler pds=new PhotoDimScaler(dimensions);
			scaledDims=pds.scaleTo(maxDims);
		} else {
			// If either of the values is null, make sure the scaled
			// dimensions is equal to the existing dimensions.  i.e. if the
			// max dimensions is null, the scaledDims will be available as
			// the normal size.  If the dimensions are unknown, so will the
			// scaled dimensions be.
			scaledDims=dimensions;
		}
	}

	/**
	 * Get the data for the given ID and calculate the scaled image size
	 * down to fit within the provided dimensions.
	 */
	public static PhotoImageData getData(int id, PhotoDimensions maxDims)
		throws Exception {

		SpyCache sc=SpyCache.getInstance();

		String key="photo_idata_" + id;
		PhotoImageData rv=(PhotoImageData)sc.get(key);
		if(rv==null) {
			rv=getDataFromDB(id);

			// Cache it for an hour.
			sc.store(key, rv, 3600000);
		}

		// Clone it so we can shove in our dimensions without affecting
		// other instances.
		rv=(PhotoImageData)rv.clone();
		if(maxDims!=null) {
			rv.setMaxDims(maxDims);
		}

		return(rv);
	}

	/**
	 * Get the data for the given ID.
	 */
	public static PhotoImageData getData(int id) throws Exception {
		return(getData(id, null));
	}

	// Go get the live data.
	private static PhotoImageData getDataFromDB(int id) throws Exception {
		PhotoImageData rv=new PhotoImageData();

		GetPhotoInfo db=new GetPhotoInfo(new PhotoConfig());
		db.setPhotoId(id);

		ResultSet rs=db.executeQuery();
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
	 * Get the dimensions of this image.
	 */
	public PhotoDimensions getDimensions() {
		return(dimensions);
	}

	/**
	 * Get the dimensions of this image's thumbnail.
	 */
	public PhotoDimensions getTnDims() {
		return(tnDims);
	}

	/**
	 * Get the dimensions of this image scaled for viewing.
	 */
	public PhotoDimensions getScaledDims() {
		return(scaledDims);
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
	 *
	 * @return Format:  yyyy-MM-dd
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

	/**
	 * Set the search ID if this is used in a search result.
	 */
	public void setSearchId(int searchId) {
		this.searchId=searchId;
	}

	/**
	 * Get the search result ID.
	 *
	 * @return -1 if this was not used in a search.
	 */
	public int getSearchId() {
		return(searchId);
	}

}
// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: PhotoImageData.java,v 1.13 2003/07/26 08:38:27 dustin Exp $

package net.spy.photo;

import java.io.Serializable;
import java.io.ObjectStreamException;
import java.io.InvalidObjectException;

import java.util.Collection;
import java.util.TreeSet;
import java.util.Map;
import java.util.HashMap;
import java.util.Date;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import net.spy.SpyObject;
import net.spy.db.SpyDB;

import net.spy.cache.SpyCache;

import net.spy.photo.sp.GetPhotoInfo;
import net.spy.photo.sp.GetAlbumKeywords;

/**
 * This class represents, and retreives all useful data for a given image.
 */
public class PhotoImageDataImpl extends SpyObject
	implements Serializable, PhotoImageData {

	private static final String CACHE_KEY="image_data";
	private static final long CACHE_TIME=3600000;

	private int id=-1;
	private Collection keywords=null;
	private String descr=null;
	private String catName=null;
	private int catId=-1;
	private int size=-1;

	// Dimensions of the full size image.
	private PhotoDimensions dimensions=null;

	// Dimensions of the thumbnail of this image.
	private PhotoDimensions tnDims=null;

	private PhotoUser addedBy=null;

	private Date timestamp=null;
	private Date taken=null;

	private Format format=null;

	private PhotoImageDataImpl(ResultSet rs) throws Exception {
		super();
		initFromResultSet(rs);
		keywords=new TreeSet();
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
		descr=rs.getString("descr");
		catName=rs.getString("catname");
		timestamp=rs.getTimestamp("ts");
		taken=rs.getDate("taken");

		// Look up the format
		format=Format.getFormat(rs.getInt("format_id"));

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
			PhotoConfig conf=PhotoConfig.getInstance();
			PhotoDimensions tdim=new PhotoDimensionsImpl(
				conf.get("thumbnail_size"));

			// Scale it down
			tnDims=PhotoDimScaler.scaleTo(dimensions, tdim);
		}
	}

	private static Map getCache() throws Exception {
		SpyCache sc=SpyCache.getInstance();
		Map rv=(Map)sc.get(CACHE_KEY);
		if(rv == null) {
			rv=initFromDB();
			sc.store(CACHE_KEY, rv, CACHE_TIME);
		}
		return(rv);
	}

	/** 
	 * Clear the image data cache.
	 */
	public static void clearCache() {
		SpyCache sc=SpyCache.getInstance();
		sc.uncache(CACHE_KEY);
	}

	/**
	 * Get the data for the given ID and calculate the scaled image size
	 * down to fit within the provided dimensions.
	 */
	public static PhotoImageData getData(int id) throws Exception {
		Map m=getCache();
		PhotoImageData rv=(PhotoImageData)m.get(new Integer(id));
		if(rv == null) {
			throw new Exception("No such image:  " + id);
		}
		return(rv);
	}

	// Go get the live data.
	private static Map initFromDB() throws Exception {
		Map rv=new HashMap();

		GetPhotoInfo db=new GetPhotoInfo(PhotoConfig.getInstance());

		ResultSet rs=db.executeQuery();
		while(rs.next()) {
			PhotoImageDataImpl pidi=new PhotoImageDataImpl(rs);
			rv.put(new Integer(pidi.getId()), pidi);
		}
		rs.close();
		db.close();

		GetAlbumKeywords gkdb=new GetAlbumKeywords(PhotoConfig.getInstance());
		rs=gkdb.executeQuery();
		while(rs.next()) {
			int photoid=rs.getInt("album_id");
			int keywordid=rs.getInt("word_id");
			PhotoImageDataImpl pidi=(PhotoImageDataImpl)rv.get(
				new Integer(photoid));
			if(pidi == null) {
				throw new Exception("Invalid keymap entry to " + photoid);
			}
			// Map it in
			pidi.keywords.add(Keyword.getKeyword(keywordid));
		}
		rs.close();
		gkdb.close();

		return(rv);
	}

	/**
	 * Get the keywords for this photo.
	 */
	public Collection getKeywords() {
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
	 * Get the PhotoUser object representing the user who added this photo.
	 */
	public PhotoUser getAddedBy() {
		return(addedBy);
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
	public Date getTaken() {
		return(taken);
	}

	/**
	 * Get the timestamp this photo was added.
	 */
	public Date getTimestamp() {
		return(timestamp);
	}

	/**
	 * Get the ID of this image.
	 */
	public int getId() {
		return(id);
	}

	/** 
	 * Get the format of this image.
	 */
	public Format getFormat() {
		return(format);
	}

	// Serialization voodoo
	private Object writeReplace() throws ObjectStreamException {
		return(new SerializedForm(id));
	}

	private static class SerializedForm implements Serializable {
		private int imgId=0;

		public SerializedForm(int i) {
			super();
			this.imgId=i;
		}

		private Object readResolve() throws ObjectStreamException {
			PhotoImageData rv=null;
			try {
				rv=PhotoImageDataImpl.getData(imgId);
			} catch(Exception e) {
				InvalidObjectException t=new InvalidObjectException(
					"Problem resolving ImageData");
				t.initCause(e);
				throw t;
			}
			return(rv);
		}
	}

}

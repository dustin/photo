// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>

package net.spy.photo.impl;

import java.io.InvalidObjectException;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.TreeSet;

import net.spy.SpyObject;
import net.spy.photo.AnnotatedRegion;
import net.spy.photo.Format;
import net.spy.photo.Keyword;
import net.spy.photo.PhotoConfig;
import net.spy.photo.PhotoDimensions;
import net.spy.photo.PhotoImage;
import net.spy.photo.PhotoImageFactory;
import net.spy.photo.PhotoImageHelper;
import net.spy.photo.PhotoUtil;
import net.spy.photo.Place;
import net.spy.photo.PlaceFactory;
import net.spy.photo.User;
import net.spy.photo.Vote;
import net.spy.photo.Votes;
import net.spy.photo.util.MetaDataExtractor;

/**
 * This class represents, and retreives all useful data for a given image.
 */
public abstract class PhotoImageImpl extends SpyObject
	implements Serializable, PhotoImage {

	private int id=-1;
	private Collection<AnnotatedRegion> annotations=null;
	private Collection<Keyword> keywords=null;
	private Votes votes=null;
	private String descr=null;
	private String catName=null;
	private String md5=null;
	private int catId=-1;
	private int size=-1;
	private int placeId=0;
	Map<String, String> metaData=null;

	// Dimensions of the full size image.
	private PhotoDimensions dimensions=null;

	// Dimensions of the thumbnail of this image.
	private PhotoDimensions tnDims=null;

	private User addedBy=null;

	private Date timestamp=null;
	private Date taken=null;

	private Format format=null;

	protected PhotoImageImpl() throws Exception {
		super();
		keywords=new TreeSet<Keyword>();
		annotations=new HashSet<AnnotatedRegion>();
		votes=new Votes();
	}

	/**
	 * String me.
	 */
	@Override
	public String toString() {
		return("{PhotoImage id=" + id + " - " + dimensions + "}");
	}

	/**
	 * True if the given object is a PhotoImage object representing the
	 * same image.
	 */
	@Override
	public boolean equals(Object o) {
		boolean rv=false;

		if(o instanceof PhotoImage) {
			PhotoImage pid=(PhotoImage)o;

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
	@Override
	public int hashCode() {
		return(id);
	}

	/** 
	 * Calculate the thumbnail size.
	 */
	protected void calculateThumbnail() {
		if(dimensions!=null) {
			// get the optimal thumbnail dimensions
			PhotoConfig conf=PhotoConfig.getInstance();
			PhotoDimensions tdim=new PhotoDimensionsImpl(
				conf.get("thumbnail_size"));

			// Scale it down
			tnDims=PhotoUtil.scaleTo(dimensions, tdim);
		}
	}

	/** 
	 * Get the annotated regions for this image.
	 */
	public Collection<AnnotatedRegion> getAnnotations() {
		return(annotations);
	}

	/** 
	 * Add an annotated region for this image.
	 */
	protected void addAnnotation(AnnotatedRegion a) {
		annotations.add(a);
	}

	/**
	 * Add a vote for this image.
	 */
	protected void addVote(Vote v) {
		votes.add(v);
	}

	/**
	 * Get the votes for this image.
	 */
	public Votes getVotes() {
		return(votes);
	}

	/**
	 * Get the keywords for this photo.
	 */
	public Collection<Keyword> getKeywords() {
		return(keywords);
	}

	/** 
	 * Add a keyword to this photo.
	 */
	protected void addKeyword(Keyword k) {
		keywords.add(k);
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
	 * Get the User object representing the user who added this photo.
	 */
	public User getAddedBy() {
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

	/** 
	 * Set the ID.
	 */
	protected void setId(int to) {
		this.id=to;
	}

	/** 
	 * Set the description.
	 */
	protected void setDescr(String to) {
		this.descr=to;
	}

	/** 
	 * Set the category name.
	 */
	protected void setCatName(String to) {
		catName=to;
	}

	/**
	 * Set the md5 of this image data.
	 */
	protected void setMd5(String to) {
		md5=to;
	}

	/* (non-Javadoc)
	 * @see net.spy.photo.PhotoImage#getMd5()
	 */
	public String getMd5() {
		return md5;
	}

	/** 
	 * Set the category ID.
	 */
	protected void setCatId(int to) {
		this.catId=to;
	}

	/** 
	 * Set the image size.
	 */
	protected void setSize(int to) {
		this.size=to;
	}

	/** 
	 * Set the image dimensions.
	 */
	protected void setDimensions(PhotoDimensions to) {
		this.dimensions=to;
	}

	/** 
	 * Set the timestamp this image was acquired.
	 */
	protected void setTimestamp(Date to) {
		// Make sure this is exactly a java.util.Date.  You can compare a Date
		// to a timestamp, but you can't compare a timetamp to a date.  yay
		this.timestamp=new Date(to.getTime());
	}

	/** 
	 * Set the date this image was taken.
	 */
	protected void setTaken(Date to) {
		this.taken=to;
	}

	/** 
	 * Set the format of this image.
	 */
	protected void setFormat(Format to) {
		this.format=to;
	}

	public Place getPlace() {
		return placeId > 0
			? PlaceFactory.getInstance().getObject(placeId) : null;
	}

	protected void setPlace(Place to) {
		placeId=to == null ? 0 : to.getId();
	}

	/**
	 * Get the MetaData for this image.
	 * @return the MetaData, or null if MetaData can't be found for this image
	 * @throws Exception if there's a problem processing this image
	 */
	public Map<String, String> getMetaData() throws Exception {
		// Memoize the meta data
		if(metaData==null) {
			metaData=Collections.emptyMap();
			if(format == Format.JPEG) {
				PhotoImageHelper p=PhotoImageHelper.getInstance();
				byte[] image=p.getImage(this);
				metaData=MetaDataExtractor.getInstance().getMetaData(image);
			}
		}
		return(metaData);
	}

	/**
	 * Get all of the variants for this image.
	 */
	public Collection<PhotoImage> getVariants() {
		return Collections.emptyList();
	}

	/** 
	 * Set the user who added this image.
	 */
	protected void setAddedBy(User to) {
		this.addedBy=to;
	}

	// Serialization voodoo
	protected abstract Object writeReplace() throws ObjectStreamException;
	/*
	private Object writeReplace() throws ObjectStreamException {
		return(new SerializedForm(getId()));
	}
	*/

	/** 
	 * Serialized form of a PhotoImageImpl.
	 */
	public static class SerializedForm implements Serializable {
		private int imgId=0;

		public SerializedForm(int i) {
			super();
			this.imgId=i;
		}

		private Object readResolve() throws ObjectStreamException {
			PhotoImage rv=null;
			try {
				PhotoImageFactory pidf=PhotoImageFactory.getInstance();
				rv=pidf.getObject(imgId);
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

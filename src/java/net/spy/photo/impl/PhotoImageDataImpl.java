// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
// arch-tag: 12030EE8-5D6D-11D9-A9A6-000A957659CC

package net.spy.photo.impl;

import java.io.ByteArrayInputStream;
import java.io.InvalidObjectException;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import net.spy.SpyObject;
import net.spy.photo.AnnotatedRegion;
import net.spy.photo.Format;
import net.spy.photo.Keyword;
import net.spy.photo.PhotoConfig;
import net.spy.photo.PhotoDimensions;
import net.spy.photo.PhotoImage;
import net.spy.photo.PhotoImageData;
import net.spy.photo.PhotoImageDataFactory;
import net.spy.photo.PhotoImageHelper;
import net.spy.photo.PhotoUtil;
import net.spy.photo.User;
import net.spy.photo.Vote;
import net.spy.photo.Votes;

import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;

/**
 * This class represents, and retreives all useful data for a given image.
 */
public abstract class PhotoImageDataImpl extends SpyObject
	implements Serializable, PhotoImageData {

	private int id=-1;
	private Collection<AnnotatedRegion> annotations=null;
	private Collection<Keyword> keywords=null;
	private Votes votes=null;
	private String descr=null;
	private String catName=null;
	private int catId=-1;
	private int size=-1;
	Map<String, Object> metaData=null;

	// Dimensions of the full size image.
	private PhotoDimensions dimensions=null;

	// Dimensions of the thumbnail of this image.
	private PhotoDimensions tnDims=null;

	private User addedBy=null;

	private Date timestamp=null;
	private Date taken=null;

	private Format format=null;

	protected PhotoImageDataImpl() throws Exception {
		super();
		keywords=new TreeSet<Keyword>();
		annotations=new HashSet<AnnotatedRegion>();
		votes=new Votes();
	}

	/**
	 * String me.
	 */
	public String toString() {
		return("{PhotoImageData id=" + id + " - " + dimensions + "}");
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
		this.catName=to;
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

	/**
	 * Get the MetaData for this image.
	 * @return the MetaData, or null if MetaData can't be found for this image
	 * @throws Exception if there's a problem processing this image
	 */
	public Map<String, Object> getMetaData() throws Exception {
		// Memoize the meta data
		if(metaData==null) {
			metaData=Collections.emptyMap();
			if(format == Format.JPEG) {
				metaData=new TreeMap<String, Object>();
				PhotoImageHelper p=new PhotoImageHelper(getId());
				PhotoImage image=p.getImage();
				ByteArrayInputStream bis=new ByteArrayInputStream(image.getData());
				Metadata md=JpegMetadataReader.readMetadata(bis);
				for(Iterator i=md.getDirectoryIterator(); i.hasNext();) {
					Directory d=(Directory)i.next();
					for(Iterator ti=d.getTagIterator(); ti.hasNext();) {
						Tag t=(Tag)ti.next();
						Object o=metaData.put(t.getTagName(), t.getDescription());
						if(o != null) {
							getLogger().warn("Duplicate tag on " + getId()
									+ ":  " + t.getTagName() + " -> " + o);
						}
					}
				}
			}
		}
		return(metaData);
	}

	/**
	 * Get all of the variants for this image.
	 */
	public Collection<PhotoImageData> getVariants() {
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
	 * Serialized form of a PhotoImageDataImpl.
	 */
	public static class SerializedForm implements Serializable {
		private int imgId=0;

		public SerializedForm(int i) {
			super();
			this.imgId=i;
		}

		private Object readResolve() throws ObjectStreamException {
			PhotoImageData rv=null;
			try {
				PhotoImageDataFactory pidf=PhotoImageDataFactory.getInstance();
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

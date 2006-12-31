// Copyright (c) 2004  Dustin Sallings <dustin@spy.net>

package net.spy.photo;

import java.util.Collection;
import java.util.Date;
import java.util.Map;

/**
 * PhotoImageData interface.
 */
public interface PhotoImageData extends Instance {

	/** 
	 * Get the annotated regions for this image.
	 */
	Collection<AnnotatedRegion> getAnnotations();

	/** 
	 * Get the keywords for this image.
	 */
	Collection<Keyword> getKeywords();

	/** 
	 * get the votes for this image.
	 */
	Votes getVotes();

	/** 
	 * Get the description of this image.
	 */
	String getDescr();

	/** 
	 * Get the ID of the category of this image.
	 */
	int getCatId();

	/** 
	 * Get the size (in bytes) of this image.
	 */
	int getSize();

	/** 
	 * Get the dimensions of this image.
	 */
	PhotoDimensions getDimensions();

	/** 
	 * Get the dimensions of the thumbnail of this image.
	 */
	PhotoDimensions getTnDims();

	/** 
	 * Get the user who added this image.
	 */
	User getAddedBy();

	/** 
	 * Get the name of the category of this image.
	 */
	String getCatName();

	/** 
	 * Get the date this image was taken/created.
	 */
	Date getTaken();

	/** 
	 * Get the timestamp of this image.
	 */
	Date getTimestamp();

	/** 
	 * Get the format of this image.
	 */
	Format getFormat();
	
	/**
	 * Get the metadata for this photo.
	 * @throws Exception 
	 */
	Map<String, String> getMetaData() throws Exception;

	/**
	 * Get all of the variations of this image.
	 */
	Collection<PhotoImageData> getVariants();

	/**
	 * Get the place this picture was taken.
	 */
	Place getPlace();

}

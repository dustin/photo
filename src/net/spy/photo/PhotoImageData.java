// Copyright (c) 2004  Dustin Sallings <dustin@spy.net>

package net.spy.photo;

import java.util.Collection;
import java.util.Date;

/**
 * PhotoImageData interface.
 */
public interface PhotoImageData {

	/** 
	 * Get the keywords for this image (space separated).
	 */
	Collection getKeywords();

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
	PhotoUser getAddedBy();

	/** 
	 * Get the name of the category of this image.
	 */
	String getCatName();

	/** 
	 * Get the date this image was taken/created (yyyy-MM-dd).
	 */
	Date getTaken();

	/** 
	 * Get the timestamp of this image.
	 */
	Date getTimestamp();

	/** 
	 * Get the ID of this image.
	 */
	int getId();

	/** 
	 * Get the format of this image.
	 */
	Format getFormat();

}

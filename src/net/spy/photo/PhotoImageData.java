// Copyright (c) 2004  Dustin Sallings <dustin@spy.net>

package net.spy.photo;

/**
 * PhotoImageData interface.
 */
public interface PhotoImageData {

	String getKeywords();

	String getDescr();

	int getCatId();

	int getSize();

	PhotoDimensions getDimensions();

	PhotoDimensions getTnDims();

	PhotoUser getAddedBy();

	String getTimestamp();

	String getCatName();

	String getTaken();

	int getId();

}

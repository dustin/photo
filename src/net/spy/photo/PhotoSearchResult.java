// Copyright (c) 2004  Dustin Sallings <dustin@spy.net>

package net.spy.photo;

import java.io.Serializable;

import java.util.Date;

/**
 * A search result.
 */
public class PhotoSearchResult extends Object
	implements PhotoImageData, Serializable {

	private PhotoImageData imgData=null;
	private int searchId=0;

	/**
	 * Get an instance of PhotoSearchResult.
	 */
	public PhotoSearchResult(PhotoImageData pid, int id) {
		super();

		imgData=pid;
		searchId=id;
	}

	/** 
	 * Get the search ID.
	 */
	public int getSearchId() {
		return(searchId);
	}

	public String getKeywords() {
		return(imgData.getKeywords());
	}

	public String getDescr() {
		return(imgData.getDescr());
	}

	public int getCatId() {
		return(imgData.getCatId());
	}

	public int getSize() {
		return(imgData.getSize());
	}

	public PhotoDimensions getDimensions() {
		return(imgData.getDimensions());
	}

	public PhotoDimensions getTnDims() {
		return(imgData.getTnDims());
	}

	public PhotoUser getAddedBy() {
		return(imgData.getAddedBy());
	}

	public String getCatName() {
		return(imgData.getCatName());
	}

	public Date getTaken() {
		return(imgData.getTaken());
	}

	public Date getTimestamp() {
		return(imgData.getTimestamp());
	}

	public int getId() {
		return(imgData.getId());
	}

}

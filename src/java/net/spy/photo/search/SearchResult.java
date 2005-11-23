// Copyright (c) 2004  Dustin Sallings <dustin@spy.net>
// arch-tag: 3611B1D4-5D6D-11D9-9C40-000A957659CC

package net.spy.photo.search;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;

import net.spy.photo.AnnotatedRegion;
import net.spy.photo.Format;
import net.spy.photo.Keyword;
import net.spy.photo.PhotoDimensions;
import net.spy.photo.PhotoImageData;
import net.spy.photo.User;
import net.spy.photo.Votes;

/**
 * A search result.
 */
public class SearchResult extends Object
	implements PhotoImageData, Serializable {

	private PhotoImageData imgData=null;
	private int searchId=0;

	/**
	 * Get an instance of SearchResult.
	 */
	public SearchResult(PhotoImageData pid, int id) {
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

	public Collection<AnnotatedRegion> getAnnotations() {
		return(imgData.getAnnotations());
	}

	public Collection<Keyword> getKeywords() {
		return(imgData.getKeywords());
	}

	public Votes getVotes() {
		return(imgData.getVotes());
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

	public User getAddedBy() {
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

	public Format getFormat() {
		return(imgData.getFormat());
	}

}

// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: PhotoSessionData.java,v 1.2 2002/02/24 01:11:52 dustin Exp $

package net.spy.photo;

/**
 * Session data goes here.
 */
public class PhotoSessionData extends Object implements java.io.Serializable {

	private boolean xmlraw=false;
	private String stylesheet=null;
	private PhotoUser user=null;
	// Search results go here
	private PhotoSearchResults results=null;
	// A cursor for looking at image comments.
	private Cursor comments=null;
	private int searchId=0;
	private String encodedSearch=null;
	private boolean isadmin=false;
	private PhotoDimensions optimalDimensions=null;

	/**
	 * Get an instance of PhotoSessionData.
	 */
	public PhotoSessionData() {
		super();
	}

	public boolean isXmlraw() {
		return(xmlraw);
	}

	public void setXmlraw(boolean xmlraw) {
		this.xmlraw=xmlraw;
	}

	public String getStylesheet() {
		return(stylesheet);
	}

	public void setStylesheet(String stylesheet) {
		this.stylesheet=stylesheet;
	}

	public PhotoUser getUser() {
		return(user);
	}

	public void setUser(PhotoUser user) {
		this.user=user;
	}

	public PhotoSearchResults getResults() {
		return(results);
	}

	public void setResults(PhotoSearchResults results) {
		this.results=results;
	}

	/**
	 * Set the comments list.
	 */
	public void setComments(Cursor comments) {
		this.comments=comments;
	}

	/**
	 * Get the comments list.
	 */
	public Cursor getComments() {
		return(comments);
	}

	public int getSearchId() {
		return(searchId);
	}

	public void setSearchId(int searchId) {
		this.searchId=searchId;
	}

	public String getEncodedSearch() {
		return(encodedSearch);
	}

	public void setEncodedSearch(String encodedSearch) {
		this.encodedSearch=encodedSearch;
	}

	public boolean isAdmin() {
		return(isadmin);
	}

	public void setIsadmin(boolean isadmin) {
		this.isadmin=isadmin;
	}

	public PhotoDimensions getOptimalDimensions() {
		return(optimalDimensions);
	}

	public void setOptimalDimensions(PhotoDimensions optimalDimensions) {
		this.optimalDimensions=optimalDimensions;
	}

}

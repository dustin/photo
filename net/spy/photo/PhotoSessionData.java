// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: PhotoSessionData.java,v 1.4 2002/02/25 08:41:47 dustin Exp $

package net.spy.photo;

/**
 * Session data goes here.
 */
public class PhotoSessionData extends Object implements java.io.Serializable {

	public static final int NOADMIN=0;
	public static final int ADMIN=1;
	public static final int SUBADMIN=2;

	private boolean xmlraw=false;
	private String stylesheet=null;
	private PhotoUser user=null;
	// Search results go here
	private PhotoSearchResults results=null;
	// A cursor for looking at image comments.
	private Cursor comments=null;
	private String encodedSearch=null;
	private PhotoDimensions optimalDimensions=null;

	private int admin_type=NOADMIN;

	/**
	 * Get an instance of PhotoSessionData.
	 */
	public PhotoSessionData() {
		super();
	}

	/**
	 * True if this user wants raw XML to be processed on the client.
	 */
	public boolean isXmlraw() {
		return(xmlraw);
	}

	/**
	 * Set to true if the user wants raw XML to be processed on the client.
	 */
	public void setXmlraw(boolean xmlraw) {
		this.xmlraw=xmlraw;
	}

	/**
	 * Get the stylesheet this session is using.
	 */
	public String getStylesheet() {
		return(stylesheet);
	}

	/**
	 * Set the stylesheet this session is using.
	 */
	public void setStylesheet(String stylesheet) {
		this.stylesheet=stylesheet;
	}

	/**
	 * Get the user who owns this session.
	 */
	public PhotoUser getUser() {
		return(user);
	}

	/**
	 * Set the user who owns this session.
	 */
	public void setUser(PhotoUser user) {
		this.user=user;
	}

	/**
	 * Get the search results that are currently being accessed.
	 */
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

	/**
	 * Get the encoded version of the current search (for saving or
	 * whatever).
	 */
	public String getEncodedSearch() {
		return(encodedSearch);
	}

	/**
	 * Set the encoded search (for saving or whatever).
	 */
	public void setEncodedSearch(String encodedSearch) {
		this.encodedSearch=encodedSearch;
	}

	/**
	 * Check to see if a specific flag is set.
	 *
	 * @return true if the flag is set.
	 */
	public boolean checkAdminFlag(int flag) {
		return( (admin_type & flag) == flag);
	}

	/**
	 * Get all the admin bits.
	 */
	public int getAdmin() {
		return(admin_type);
	}

	/**
	 * Test for the ADMIN bit.
	 */
	/*
	public boolean isAdmin() {
		return(checkAdminFlag(ADMIN));
	}
	*/

	/**
	 * Set the ADMIN bit.
	 */
	/*
	public void setIsadmin(boolean isadmin) {
		setAdmin(ADMIN);
	}
	*/

	/**
	 * Set all the admin bits.
	 */
	public void setAdmin(int to) {
		admin_type=to;
	}

	/**
	 * Get the optimal dimensions for this session.
	 */
	public PhotoDimensions getOptimalDimensions() {
		return(optimalDimensions);
	}

	/**
	 * Set the optimal dimensions for this session.
	 */
	public void setOptimalDimensions(PhotoDimensions optimalDimensions) {
		this.optimalDimensions=optimalDimensions;
	}

}

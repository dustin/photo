// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: PhotoSessionData.java,v 1.13 2002/07/10 03:38:08 dustin Exp $

package net.spy.photo;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

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

	private String encodedSearch=null;
	private PhotoDimensions optimalDimensions=null;

	private Map cursors=null;

	private int admin_type=NOADMIN;

	// Keep track of how many images have been served up to this user.
	private int imagesSeen=0;
    // Keep track of the last image the user viewed.
    private int lastImageSeen=0;

	/**
	 * Get an instance of PhotoSessionData.
	 */
	public PhotoSessionData() {
		super();
		cursors=Collections.synchronizedMap(new HashMap());
	}

	/**
	 * String me.
	 */
	public String toString() {
		return("{PhotoSessionData for " + user + "}");
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
	 *
	 * Also, drop any administrative privs.
	 */
	public void setUser(PhotoUser user) {
		this.user=user;
		unSetAdmin();
	}

	/**
	 * Get the search results that are currently being accessed.
	 *
	 * @deprecated use getCursor instead
	 */
	public PhotoSearchResults getResults() {
		return((PhotoSearchResults)getCursor("searchResults"));
	}

	/**
	 * @deprecated use setCursor instead
	 */
	public void setResults(PhotoSearchResults results) {
		setCursor("searchResults", results);
	}

	/**
	 * Get a cursor by name.
	 *
	 * @return the Cursor or null if no such cursor exists
	 */
	public Cursor getCursor(String name) {
		return((Cursor)cursors.get(name));
	}

	/**
	 * Set a cursor by name.
	 */
	public void setCursor(String name, Cursor cursor) {
		cursors.put(name, cursor);
	}

	/**
	 * Set the comments list.
	 *
	 * @deprecated use setCursor instead
	 */
	public void setComments(Cursor comments) {
		setCursor("comments", comments);
	}

	/**
	 * Get the comments list.
	 *
	 * @deprecated use getCursor instead
	 */
	public Cursor getComments() {
		return(getCursor("comments"));
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
	public void setAdmin(int to) throws PhotoException {
		// Check access

		switch(to) {
			case ADMIN:
				if(!getUser().isInGroup("admin")) {
					throw new PhotoException(
						"Requested admin for non-admin user.");
				}
				break;
			case SUBADMIN:
				if(!getUser().isInGroup("subadmin")) {
					throw new PhotoException(
						"Requested subadmin for non-admin user.");
				}
				break;
			case NOADMIN:
				// Anyone can revoke admin privs
				admin_type=NOADMIN;
				break;
			default:
				throw new PhotoException("Invalid admin type.");
		}

		// If we made it this far, adminify
		admin_type=to;
	}

	/**
	 * Set the default administrative privilege for this user.
	 */
	public void setAdmin() throws PhotoException {
		if(getUser().isInGroup("admin")) {
			setAdmin(ADMIN);
		} else if(getUser().isInGroup("subadmin")) {
			setAdmin(SUBADMIN);
		}
	}

	/**
	 * Safely revoke administrative privs.
	 */
	public void unSetAdmin() {
		admin_type=NOADMIN;
	}

	/**
	 * Get the optimal dimensions for this session.
	 * If the optimal dimensions have not yet been set for this session,
	 * initialize them to the default.
	 */
	public PhotoDimensions getOptimalDimensions() {
		// If not set, initialize.
		if(optimalDimensions == null) {
			PhotoConfig conf=new PhotoConfig();
			PhotoDimensions dim=new PhotoDimensionsImpl(
				conf.get("optimal_image_size", "800x600"));
			setOptimalDimensions(dim);
		}
		return(optimalDimensions);
	}

	/**
	 * Set the optimal dimensions for this session.
	 */
	public void setOptimalDimensions(PhotoDimensions optimalDimensions) {
		this.optimalDimensions=optimalDimensions;
	}

	/**
	 * Mark another image as having been served up.
	 */
	public synchronized void sawImage(int imageId) {
		imagesSeen++;
		lastImageSeen=imageId;
	}

	/**
	 * Get the ID of the last image this session served up.
	 */
	public int getLastImageSeen() {
		return(lastImageSeen);
	}

	/**
	 * How many images has this user seen?
	 */
	public int getImagesSeen() {
		return(imagesSeen);
	}

}

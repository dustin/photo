// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
// arch-tag: 422EEEC3-5D6D-11D9-9D27-000A957659CC

package net.spy.photo;

import java.util.Collections;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import net.spy.util.RingBuffer;

import net.spy.photo.impl.PhotoDimensionsImpl;
import net.spy.photo.search.SearchResults;

/**
 * Session data goes here.
 */
public class PhotoSessionData extends Object implements java.io.Serializable {

	/** 
	 * The session attribute name.
	 */
	public static final String SES_ATTR="photoSession";

	public static final int NOADMIN=0;
	public static final int ADMIN=1;
	public static final int SUBADMIN=2;

	private PhotoUser user=null;

	private String encodedSearch=null;
	private PhotoDimensions optimalDimensions=null;

	private Map cursors=null;

	private int adminType=NOADMIN;

	// Keep track of how many images have been served up to this user.
	private int imagesSeen=0;
    // Keep track of the last image the user viewed.
    private int lastImageSeen=0;
	private RingBuffer imagesSeenBuf=null;

	/**
	 * Get an instance of PhotoSessionData.
	 */
	public PhotoSessionData() {
		super();
		cursors=Collections.synchronizedMap(new HashMap());
		imagesSeenBuf=new RingBuffer(128);
	}

	/**
	 * String me.
	 */
	public String toString() {
		return("{PhotoSessionData for " + user + "}");
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
	 */
	public SearchResults getResults() {
		return((SearchResults)getCursor("searchResults"));
	}

	/**
	 * Set the results cursor.
	 */
	public void setResults(SearchResults results) {
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
	 */
	public void setComments(Cursor comments) {
		setCursor("comments", comments);
	}

	/**
	 * Get the comments list.
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
		return( (adminType & flag) == flag);
	}

	/**
	 * Get all the admin bits.
	 */
	public int getAdmin() {
		return(adminType);
	}

	/**
	 * Set all the admin bits.
	 */
	public void setAdmin(int to) throws PhotoException {
		// Check access

		switch(to) {
			case ADMIN:
				if(!getUser().isInRole("admin")) {
					throw new PhotoException(
						"Requested admin for non-admin user.");
				}
				break;
			case SUBADMIN:
				if(!getUser().isInRole("subadmin")) {
					throw new PhotoException(
						"Requested subadmin for non-admin user.");
				}
				break;
			case NOADMIN:
				// Anyone can revoke admin privs
				adminType=NOADMIN;
				break;
			default:
				throw new PhotoException("Invalid admin type.");
		}

		// If we made it this far, adminify
		adminType=to;
	}

	/**
	 * Set the default administrative privilege for this user.
	 */
	public void setAdmin() throws PhotoException {
		if(getUser().isInRole("admin")) {
			setAdmin(ADMIN);
		} else if(getUser().isInRole("subadmin")) {
			setAdmin(SUBADMIN);
		}
	}

	/**
	 * Safely revoke administrative privs.
	 */
	public void unSetAdmin() {
		adminType=NOADMIN;
	}

	/**
	 * Get the optimal dimensions for this session.
	 * If the optimal dimensions have not yet been set for this session,
	 * initialize them to the default.
	 */
	public PhotoDimensions getOptimalDimensions() {
		// If not set, initialize.
		if(optimalDimensions == null) {
			PhotoConfig conf=PhotoConfig.getInstance();
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
		imagesSeenBuf.add(new Integer(imageId));
	}

	/**
	 * Get the ID of the last image this session served up.
	 */
	public synchronized int getLastImageSeen() {
		return(lastImageSeen);
	}

	/**
	 * How many images has this user seen?
	 */
	public synchronized int getImagesSeen() {
		return(imagesSeen);
	}

	/** 
	 * Get a Collection of the images this user session has seen.
	 */
	public synchronized Collection getImageSeenCollection() {
		return(imagesSeenBuf);
	}

}

// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: PhotoACLEntry.java,v 1.1 2001/12/28 12:39:37 dustin Exp $

package net.spy.photo;

/**
 * An ACL entry.
 */
public class PhotoACLEntry extends Object implements java.io.Serializable {

	private int uid=0;
	private int cat=0;
	private boolean canview=false;
	private boolean canadd=false;

	/**
	 * Get an instance of PhotoACLEntry.
	 */
	public PhotoACLEntry(int uid, int cat) {
		super();
		this.uid=uid;
		this.cat=cat;
	}

	/**
	 * Get the UID this ACL entry represents.
	 */
	public int getUid() {
		return(uid);
	}

	/**
	 * Get the Category ID this ACL entry represents.
	 */
	public int getCat() {
		return(cat);
	}

	/**
	 * Does this ACL entry permit adding?
	 */
	public boolean canAdd() {
		return(canadd);
	}

	/**
	 * Does this ACL entry permit viewing?
	 */
	public boolean canView() {
		return(canview);
	}

	/**
	 * Set the CanView value of this ACL entry.
	 */
	public void setCanView(boolean to) {
		this.canview=to;
	}

	/**
	 * Set the CanAdd value of this ACL entry.
	 */
	public void setCanAdd(boolean to) {
		this.canadd=to;
	}

}

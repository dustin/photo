// Copyright (c) 2004  Dustin Sallings <dustin@spy.net>
// arch-tag: F2081EF6-5D6C-11D9-9C1C-000A957659CC

package net.spy.photo;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import java.io.Serializable;

import net.spy.SpyObject;

/**
 * An ACL holding a collection of operations.
 */
public class PhotoACL extends SpyObject implements Serializable {

	private List acl=null;
	private boolean modified=false;

	/**
	 * Get an instance of PhotoACL.
	 */
	public PhotoACL() {
		super();
		acl=new ArrayList();
	}

	/** 
	 * Iterate this ACL.
	 * @return an unmodifiable iterator of the ACL entries.
	 */
	public Iterator iterator() {
		return(Collections.unmodifiableCollection(acl).iterator());
	}

	/** 
	 * String me.
	 * 
	 * @return 
	 */
	public String toString() {
		StringBuffer sb=new StringBuffer(64);
		sb.append("{PhotoACL entries=");
		sb.append(acl);
		sb.append("}");
		return(sb.toString());
	}

	/** 
	 * Get the ACL entry for the given id.
	 * 
	 * @param what the given id
	 * @param create if an entry doesn't exist and this is true, create an acl
	 * @return the entry, or null if one doesn't exist and create is false
	 */
	public PhotoACLEntry getEntry(int what, boolean create) {
		PhotoACLEntry rv=null;

		for(Iterator i=acl.iterator(); rv==null && i.hasNext();) {
			PhotoACLEntry ae=(PhotoACLEntry)i.next();
			if(ae.getWhat() == what) {
				rv=ae;
			}
		}

		if(rv == null && create) {
			rv=new PhotoACLEntry(what);
			setModified(true);
			acl.add(rv);
		}

		return(rv);
	}

	/** 
	 * Get the ACL entry for the given ID.  Do not create a new one if one
	 * doesn't exist.
	 * 
	 * @param what the id
	 * @return the PhotoACLEntry, or null if one doesn't exist
	 */
	public PhotoACLEntry getEntry(int what) {
		return(getEntry(what, false));
	}

	/** 
	 * Add a view ACL entry.
	 */
	public void addViewEntry(int what) {
		PhotoACLEntry aclEntry=getEntry(what, true);
		aclEntry.setCanView(true);
		setModified(true);
	}

	/** 
	 * Add an ``add'' entry for this device.
	 */
	public void addAddEntry(int what) {
		PhotoACLEntry aclEntry=getEntry(what, true);
		aclEntry.setCanAdd(true);
		setModified(true);
	}

	/** 
	 * Remove the acl entry for the given category.
	 */
	public void removeEntry(int what) {
		PhotoACLEntry entry=getEntry(what, false);
		if(entry!=null) {
			acl.remove(entry);
			setModified(true);
		}
	}

	/** 
	 * Remove all ACL entries.
	 */
	public void removeAllEntries() {
		acl.clear();
		setModified(true);
	}

	/** 
	 * Convenience method for determining whether a category can be viewed.
	 */
	public boolean canView(int what) {
		boolean rv=false;
		PhotoACLEntry ae=getEntry(what, false);
		if(ae != null && ae.canView()) {
			rv=true;
		}
		return(rv);
	}

	/** 
	 * Convenience method for determining whether a category can have stuff
	 * added to it.
	 */
	public boolean canAdd(int what) {
		boolean rv=false;
		PhotoACLEntry ae=getEntry(what, false);
		if(ae != null && ae.canAdd()) {
			rv=true;
		}
		return(rv);
	}

	private void setModified(boolean to) {
		modified=to;
	}

	/** 
	 * Set the modified flag.
	 */
	public boolean isModified() {
		return(modified);
	}

}

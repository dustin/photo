// Copyright (c) 2004  Dustin Sallings <dustin@spy.net>
// arch-tag: F2081EF6-5D6C-11D9-9C1C-000A957659CC

package net.spy.photo;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.spy.SpyObject;

/**
 * An ACL holding a collection of operations.
 */
public class PhotoACL extends SpyObject
	implements Serializable, Iterable<PhotoACLEntry> {

	private Map<Integer, PhotoACLEntry> acl=null;
	private boolean modified=false;

	/**
	 * Get an instance of PhotoACL.
	 */
	public PhotoACL() {
		super();
		acl=new HashMap<Integer, PhotoACLEntry>();
	}

	/** 
	 * Get a copy of this PhotoACL.
	 */
	public PhotoACL copy() {
		PhotoACL rv=new PhotoACL();
		for(Map.Entry<Integer, PhotoACLEntry> e: acl.entrySet()) {
			rv.acl.put(e.getKey(), e.getValue().copy());
		}
		return(rv);
	}

	/** 
	 * Iterate this ACL.
	 * @return an unmodifiable iterator of the ACL entries.
	 */
	public Iterator<PhotoACLEntry> iterator() {
		return(Collections.unmodifiableCollection(acl.values()).iterator());
	}

	/** 
	 * String me.
	 */
	@Override
	public String toString() {
		StringBuffer sb=new StringBuffer(64);
		sb.append("{PhotoACL entries=");
		sb.append(acl.values());
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
		PhotoACLEntry rv=acl.get(what);

		if(rv == null && create) {
			rv=new PhotoACLEntry(what);
			setModified(true);
			acl.put(what, rv);
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

// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
// arch-tag: C2FF0E34-5D6C-11D9-9841-000A957659CC

package net.spy.photo.impl;

import net.spy.SpyObject;
import net.spy.photo.Category;
import net.spy.photo.PhotoACL;

/**
 * Category representation.
 */
public abstract class AbstractCategory extends SpyObject implements Category {

	private int id=-1;
	private String name=null;

	private PhotoACL acl=null;

	/**
	 * Get an instance of Category.
	 */
	public AbstractCategory(int catId) {
		super();
		this.id=catId;
		acl=new PhotoACL();
	}

	/** 
	 * Get the hash code.
	 * 
	 * @return the ID of this category
	 */
	@Override
	public int hashCode() {
		return (getId());
	}

	/** 
	 * Return true if the object passed as an argument is a Category object
	 * with the same ID.
	 */
	@Override
	public boolean equals(Object o) {
		boolean rv=false;
		if(o instanceof Category) {
			Category cat=(Category)o;
			rv=id == cat.getId();
		}
		return(rv);
	}

	/**
	 * Get the ACL entries for this category.
	 */
	public PhotoACL getACL() {
		return(acl);
	}

	/**
	 * Get the ID of this category.
	 */
	public int getId() {
		return(id);
	}

	/**
	 * Get the name of this category.
	 */
	public String getName() {
		return(name);
	}

	/**
	 * Set the name of this category.
	 */
	protected void setName(String to) {
		this.name=to;
	}

	/**
	 * String me.
	 */
	@Override
	public String toString() {
		StringBuffer sb=new StringBuffer(64);

		sb.append("{AbstractCategory name=");
		sb.append(name);
		sb.append(", id=");
		sb.append(id);
		sb.append("}");

		return (sb.toString());
	}

}

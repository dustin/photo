// Copyright (c) 2005  Dustin Sallings <dustin@spy.net>
// arch-tag: 9AC4F3F6-5EE6-11D9-852C-000A957659CC

package net.spy.photo;

/**
 * Interface for category objects.
 */
public interface Category extends Instance {

	/** 
	 * Get the name of this category.
	 */
	String getName();

	/** 
	 * Get the ACL attached to this category.
	 */
	PhotoACL getACL();

}
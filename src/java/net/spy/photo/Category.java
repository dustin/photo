// Copyright (c) 2005  Dustin Sallings <dustin@spy.net>

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

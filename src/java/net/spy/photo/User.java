// Copyright (c) 2005  Dustin Sallings
// arch-tag: 16A61333-5FB1-11D9-A0A7-000A957659CC

// This class stores an entry from the wwwusers table.
package net.spy.photo;

import java.util.Collection;
import java.security.Principal;

import net.spy.factory.Instance;

/**
 * Represents a user in the photo system.
 */
public interface User extends Principal, Instance {

	/**
	 * Get the user's E-mail address.
	 */
	String getEmail();

	/** 
	 * Get the persistent session ID.
	 */
	String getPersess();

	/**
	 * Get the ACL list.
	 */
	PhotoACL getACL();

	/**
	 * Get a Collection of Strings describing all the roles this user has.
	 */
	Collection<String> getRoles();

	/**
	 * True if the user is in the given group.
	 */
	boolean isInRole(String roleName);

	/**
	 * True if the user can add.
	 */
	boolean canAdd();

	/**
	 * True if the user can add to the specific category.
	 */
	boolean canAdd(int cat);

	/**
	 * True if the user can view images in the specific category.
	 */
	boolean canView(int cat);

	/**
	 * Get the real name of this user.
	 */
	String getRealname();

	/**
	 * Get the user's hashed password.	Useful for administration forms and
	 * stuff.
	 */
	String getPassword();

}

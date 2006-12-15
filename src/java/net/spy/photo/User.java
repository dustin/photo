// Copyright (c) 2005  Dustin Sallings

// This class stores an entry from the wwwusers table.
package net.spy.photo;

import java.security.Principal;
import java.util.Collection;

import net.spy.factory.CacheKey;

/**
 * Represents a user in the photo system.
 */
public interface User extends Principal, Instance {

	public static final String BY_EMAIL="email";
	public static final String BY_USERNAME="username";
	public static final String BY_PERSESS="persess";

	/** 
	 * The role authenticated users have.
	 */
	static final String AUTHENTICATED="authenticated";
	/** 
	 * Role that indicates the user may add.
	 */
	static final String CANADD="canadd";
	/** 
	 * The admin role.
	 */
	static final String ADMIN="admin";
	/** 
	 * The subadmin role.
	 */
	static final String SUBADMIN="subadmin";

	/**
	 * Get the user's E-mail address.
	 */
	@CacheKey(name=BY_EMAIL)
	String getEmail();

	/** 
	 * Get the persistent session ID.
	 */
	@CacheKey(name=BY_PERSESS)
	String getPersess();

	/**
	 * Get the ACL list.
	 */
	PhotoACL getACL();

	@CacheKey(name=BY_USERNAME)
	String getName();

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

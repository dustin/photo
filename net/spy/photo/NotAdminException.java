// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: NotAdminException.java,v 1.1 2002/06/22 05:49:32 dustin Exp $

package net.spy.photo;

/**
 * Exception thrown when an admin function is requested while a user is not
 * an admin.
 */
public class NotAdminException extends PhotoException {

	/**
	 * Get an instance of NotAdminException.
	 */
	public NotAdminException() {
		super();
	}

	/**
	 * Get an instance of NotAdminException.
	 */
	public NotAdminException(String msg) {
		super(msg);
	}

}

// Copyright (c) 2003  Dustin Sallings <dustin@spy.net>
//
// $Id: PhotoUserException.java,v 1.1 2003/01/07 09:38:51 dustin Exp $

package net.spy.photo;

/**
 * Exception thrown when there's a problem initializing or getting a User.
 */
public class PhotoUserException extends PhotoException {

	/**
	 * Get an instance of PhotoUserException.
	 */
	public PhotoUserException(String msg) {
		super(msg);
	}

	/**
	 * Get an instance of PhotoUserException.
	 */
	public PhotoUserException(String msg, Throwable t) {
		super(msg, t);
	}

}

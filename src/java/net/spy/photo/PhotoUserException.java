// Copyright (c) 2003  Dustin Sallings <dustin@spy.net>

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

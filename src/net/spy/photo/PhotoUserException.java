// Copyright (c) 2003  Dustin Sallings <dustin@spy.net>
// arch-tag: 4575E83C-5D6D-11D9-AC24-000A957659CC

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

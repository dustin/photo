// Copyright (c) 2003  Dustin Sallings <dustin@spy.net>

package net.spy.photo;

/**
 * Exception thrown when there's a problem initializing or getting a User.
 */
public class NoSuchPhotoUserException extends PhotoUserException {

	/**
	 * Get an instance of NoSuchPhotoUserException.
	 */
	public NoSuchPhotoUserException(String msg) {
		super(msg);
	}

	/**
	 * Get an instance of NoSuchPhotoUserException.
	 */
	public NoSuchPhotoUserException(String msg, Throwable t) {
		super(msg, t);
	}

}

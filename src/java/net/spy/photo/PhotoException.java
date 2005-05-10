// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
// arch-tag: 06370356-5D6D-11D9-9FD6-000A957659CC

package net.spy.photo;

/**
 * Exceptions thrown by the servlet engine.
 */
public class PhotoException extends Exception {

	/**
	 * Get an instance of PhotoException.
	 */
	public PhotoException(String msg) {
		super(msg);
	}

	/**
	 * Get an instance of PhotoException.
	 */
	public PhotoException(String msg, Throwable t) {
		super(msg, t);
	}

}

// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>

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

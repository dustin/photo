/*
 * Copyright (c) 1999 Dustin Sallings
 *
 * $Id: PhotoHelper.java,v 1.11 2002/06/23 07:01:03 dustin Exp $
 */

package net.spy.photo;

import java.sql.*;
import java.util.*;

import net.spy.*;
import net.spy.log.*;

/**
 * Superclass for general supplemental classes.
 */
public class PhotoHelper extends Object { 

	private PhotoConfig conf=null;

	/**
	 * Instantiate the helper class.
	 */
	public PhotoHelper() {
		super();
		conf = new PhotoConfig();
	}

	/**
	 * Get the configuration instance contained in this helper.
	 */
	protected PhotoConfig getConfig() {
		return(conf);
	}

	/**
	 * Log a message.
	 */
	protected void log(String message) {
		System.err.println(getClass().getName() + ": " + message);
	}
}

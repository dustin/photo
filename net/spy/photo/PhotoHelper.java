/*
 * Copyright (c) 1999 Dustin Sallings
 *
 * $Id: PhotoHelper.java,v 1.9 2002/02/21 09:26:03 dustin Exp $
 */

package net.spy.photo;

import java.sql.*;
import java.util.*;
import java.lang.*;

import net.spy.*;
import net.spy.log.*;

// The class
public class PhotoHelper
{ 
	SpyLog logger=null;
	PhotoConfig conf=null;

	public PhotoHelper() throws Exception {
		super();
		conf = new PhotoConfig();
	}

	private void initlog() {
		logger = new SpyLog("PhotoLog");
	}

	/**
	 * Log a message.
	 */
	protected void log(String message) {
		if(logger==null) {
			initlog();
		}
		System.err.println("PhotoHelper: " + message);
	}
}

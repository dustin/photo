/*
 * Copyright (c) 1999 Dustin Sallings
 *
 * $Id: PhotoHelper.java,v 1.8 2002/02/15 08:28:07 dustin Exp $
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

	protected void initlog() {
		logger = new SpyLog("PhotoLog");
	}

	protected void log(String message) {
		if(logger==null) {
			initlog();
		}
		System.err.println("PhotoHelper: " + message);
	}
}

/*
 * Copyright (c) 1999 Dustin Sallings
 *
 * $Id: PhotoUtil.java,v 1.9 2001/07/19 10:07:09 dustin Exp $
 */

package net.spy.photo;

import java.lang.*;
import java.util.*;
import java.text.*;
import java.io.File;

import net.spy.*;

// The class
public class PhotoUtil extends Object { 
	// Split that shit
	public static String[] split(String on, String input) {
		return(SpyUtil.split(on, input));
	}

	// Make a strings safe for the database.
	public static String dbquote_str(String thing) {
		return(SpyDB.dbquote_str(thing));
	}

	// Tokenize a template file and return the tokenized stuff.
	public static String tokenize(PhotoSession p, String file, Hashtable vars) {
		SpyToker t=new SpyToker();
		String ret;

		PhotoConfig conf = new PhotoConfig();

		vars.put("SELF_URI", p.getSelfURI());
		vars.put("REMOTE_USER", p.getUser().getUsername());
		vars.put("REMOTE_UID", "" + p.getUser().getId());
		vars.put("LAST_MODIFIED", "recently");
		vars.put("STYLESHEET", "<link rel=\"stylesheet\"href=\""
			+ p.getSelfURI() + "?func=getstylesheet\">");
		
		ret = t.tokenize(new File(conf.get("includes") + "/" + file), vars);
		return(ret);
	}

	// Tokenize without a session.
	public static String tokenize(String file, Hashtable vars) {
		SpyToker t=new SpyToker();
		String ret=null;

		PhotoConfig conf = new PhotoConfig();
		vars.put("LAST_MODIFIED", "recently");
		ret = t.tokenize(new File(conf.get("includes") + "/" + file), vars);
		return(ret);
	}

	// Get today's date as a string
	public static String getToday() {
		Date ts=new Date();
		SimpleDateFormat f=new SimpleDateFormat("MM/dd/yyyy");
		return(f.format(ts));
	}

	public static int myHash(Hashtable in) {
		int i=0;
		for(Enumeration e=in.elements(); e.hasMoreElements(); ) {
			i+=e.nextElement().hashCode();
		}
		return(i);
	}

	// Get the id of the ``default'' user.  The default default user is
	// guest, but it can be assigned to any arbitrary user.
	public static int getDefaultId() {
		int ret=-1;

		try {
			PhotoSecurity s=new PhotoSecurity();
			PhotoConfig conf = new PhotoConfig();
			String user=conf.get("default_user", "guest");
			PhotoUser u=s.getUser(user);
			ret=u.getId();
		} catch(Exception e) {
			ret=-1;
		}

		return(ret);
	}
}

/*
 * Copyright (C) 1999  Dustin Sallings <dustin@spy.net>
 *
 * $Id: PhotoConfig.java,v 1.13 2002/06/23 07:01:03 dustin Exp $
 */

package net.spy.photo;

import net.spy.*;
import java.io.*;

/**
 * Configuration for PhotoServlet.
 */
public class PhotoConfig extends SpyConfig {

	// If provided, this configuration will be used.
	private static File staticConfigLocation=null;

	// Places to look for config files.
	private File configs[]={
		new File("photo.conf"),
		new File("/Users/dustin/prog/java/servlet/photo/photo.conf"),
		new File("/usr/local/etc/photo.conf"),
		new File("/afs/spy.net/misc/web/etc/photo.conf")
	};

	/**
	 * Get a configuration.
	 */
	public PhotoConfig() {
		super();

		boolean gotit=false;

		if(staticConfigLocation==null) {
			loadConfig(configs);
		} else {
			loadConfig(staticConfigLocation);
		}

		loadDefaults();
	}

	/**
	 * Set the configuration location for all future instances of
	 * PhotoConfig.
	 */
	public void setStaticConfigLocation(String to) {
		staticConfigLocation=new File(to);
	}

	private void loadDefaults() {
		// Now add defaults
		orput("dbDriverName", "org.postgresql.Driver");
		orput("dbSource", "jdbc:postgresql://db/photo");
		orput("dbUser", "nobody");
		orput("dbPass", "nopassword");
		orput("imageserver", "//localhost/ImageServer");
		orput("includes", "/home/dustin/public_html/jphoto/inc/");
		orput("timezone", "GMT");
		orput("cryptohash", "SHA");
		orput("storer_sleep", "10");

		orput("mail_server", "mail");
		orput("mail_sender", "dustin+photoservlet@spy.net");

		orput("thumbnail_size", "220x146");
	}
}

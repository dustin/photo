/*
 * Copyright (C) 1999  Dustin Sallings <dustin@spy.net>
 *
 * $Id: PhotoConfig.java,v 1.15 2002/07/10 03:38:08 dustin Exp $
 */

package net.spy.photo;

import java.io.File;

import net.spy.util.SpyConfig;

/**
 * Configuration for PhotoServlet.
 */
public class PhotoConfig extends SpyConfig {

	// If provided, this configuration will be used.
	private static File staticConfigLocation=null;

	private static PhotoConfig instance=null;

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
	private PhotoConfig() {
		super();

		if(staticConfigLocation==null) {
			loadConfig(configs);
		} else {
			loadConfig(staticConfigLocation);
		}

		loadDefaults();
	}

	/** 
	 * Get a PhotoConfig instance.
	 */
	public static synchronized PhotoConfig getInstance() {
		if(instance == null) {
			instance=new PhotoConfig();
		}
		return(instance);
	}

	private static synchronized void killInstance() {
		instance=null;
	}

	/**
	 * Set the configuration location for all future instances of
	 * PhotoConfig.
	 */
	public void setStaticConfigLocation(String to) {
		staticConfigLocation=new File(to);
		// Kill any given instance
		killInstance();
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

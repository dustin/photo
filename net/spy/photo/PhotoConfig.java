/*
 * Copyright (C) 1999  Dustin Sallings <dustin@spy.net>
 *
 * $Id: PhotoConfig.java,v 1.5 2000/11/10 07:17:18 dustin Exp $
 */

package net.spy.photo;

import net.spy.*;

public class PhotoConfig extends SpyConfig {

	// Places to look for config files.
	protected String configs[]={
		"photo.conf",
		"/home/dustin/prog/java/servlet/photo_xml/photo.conf",
		"/usr/local/etc/photo.conf",
		"/afs/spy.net/misc/web/etc/photo.conf"
	};

	public PhotoConfig() {
		super();

		boolean gotit=false;

		loadConfig(configs);

		// Now add defaults
		orput("dbDriverName", "org.postgresql.Driver");
		orput("dbSource", "jdbc:postgresql://localhost/photo");
		orput("dbUser", "nobody");
		orput("dbPass", "");
		orput("objectserver", "//localhost/RObjectServer");
		orput("imageserver", "//localhost/ImageServer");
		orput("includes", "/home/dustin/public_html/jphoto/inc/");
		orput("timezone", "GMT");
		orput("cryptohash", "SHA");
		orput("storer_sleep", "10");
	}
}

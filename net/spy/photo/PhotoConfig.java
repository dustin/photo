/*
 * Copyright (C) 1999  Dustin Sallings <dustin@spy.net>
 *
 * $Id: PhotoConfig.java,v 1.3 2000/06/26 06:42:31 dustin Exp $
 */

package net.spy.photo;

import net.spy.*;

public class PhotoConfig extends SpyConfig {

	// Places to look for config files.
	protected String configs[]={
		"/usr/local/etc/photo.conf",
		"/afs/spy.net/misc/web/etc/photo.conf",
		"/afs/spy.net/misc/web/etc/photoconfig.xml"
	};

	public PhotoConfig() {
		super();

		boolean gotit=false;

		// Try out a few config file until we find one.
		for(int i=0; i<configs.length && gotit==false; i++) {
			gotit=loadConfig(configs[i]);
		}

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

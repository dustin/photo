/*
 * Copyright (C) 1999  Dustin Sallings <dustin@spy.net>
 *
 * $Id: PhotoConfig.java,v 1.2 2000/06/25 07:38:32 dustin Exp $
 */

package net.spy.photo;

import net.spy.*;

public class PhotoConfig extends SpyConfig {

	public PhotoConfig() {
		super("/afs/spy.net/misc/web/etc/photoconfig.xml");
		// super("/afs/spy.net/misc/web/etc/photo.conf");
		// super("/usr/local/etc/photo.conf");
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

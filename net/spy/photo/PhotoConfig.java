/*
 * Copyright (C) 1999  Dustin Sallings <dustin@spy.net>
 *
 * $Id: PhotoConfig.java,v 1.10 2002/02/21 09:36:17 dustin Exp $
 */

package net.spy.photo;

import net.spy.*;
import java.io.*;

public class PhotoConfig extends SpyConfig {

	// Places to look for config files.
	private File configs[]={
		new File("photo.conf"),
		new File("/Users/dustin/prog/java/servlet/photo/photo.conf"),
		new File("/usr/local/etc/photo.conf"),
		new File("/afs/spy.net/misc/web/etc/photo.conf")
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

		orput("mail_server", "mail");
		orput("mail_sender", "dustin+photoservlet@spy.net");

		orput("thumbnail_size", "220x146");

		// XSLT processor
		orput("xslt_processor", "net.spy.photo.xslt.ResinXSLT");
	}
}

// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: ImageServerFactory.java,v 1.2 2002/07/10 03:38:08 dustin Exp $

package net.spy.photo;

import net.spy.log.Logger;
import net.spy.log.LoggerFactory;

/**
 * Get ImageServer objects as configured for this instance.
 */
public class ImageServerFactory extends Object {

	private static ImageServer server=null;

	// You don't instantiate this, G.
	private ImageServerFactory() {
		super();
	}

	/**
	 * Get an instance of ImageServer.
	 */
	public static synchronized ImageServer getImageServer()
		throws PhotoException {

		if(server==null) {
			try {
				getServer();
			} catch(Exception e) {
				throw new PhotoException("Couldn't get ImageServer", e);
			}
		}

		return(server);
	}

	// Load the instance
	private static void getServer() throws Exception {
		Logger log=LoggerFactory.getLogger(ImageServer.class);

		PhotoConfig conf=PhotoConfig.getInstance();
		String className=conf.get("imageserverimpl",
			"net.spy.photo.rmi.ImageServerImpl");
		log.info("Initializing " + className);
		Class c=Class.forName(className);
		server=(ImageServer)c.newInstance();
		log.info("Initialization complete.");
	}

}



// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: RemoteImageCacheImpl.java,v 1.1 2002/06/17 03:52:32 dustin Exp $

package net.spy.photo;

import java.rmi.RemoteException;

import net.spy.RHash;
import net.spy.util.NestedException;

/**
 * ImageCache that uses RMI.
 */
public class RemoteImageCacheImpl extends Object implements ImageCache {

	private RHash rhash=null;

	/**
	 * Get an instance of RemoteImageCacheImpl.
	 */
	public RemoteImageCacheImpl() throws NestedException {
		super();

		PhotoConfig conf=new PhotoConfig();
		rhash=new RHash(conf.get("rhash.url", "//localhost/RObjectServer"));
	}

	/**
	 * @see ImageCache
	 */
	public PhotoImage getImage(String key) throws PhotoException {
		return( (PhotoImage)rhash.get(key) );
	}

	/**
	 * @see ImageCache
	 */
	public void putImage(String key, PhotoImage image) throws PhotoException {
		rhash.put(key, image);
	}

}


/*
 * Copyright (c) 1999 Dustin Sallings
 *
 * $Id: PhotoCacheItem.java,v 1.2 2000/07/05 06:08:33 dustin Exp $
 */

package net.spy.photo;

import java.util.*;

public class PhotoCacheItem extends Object {
	protected Object key=null;
	protected Object value=null;
	protected long exptime=0;

	public PhotoCacheItem(Object key, Object value, long cache_time) {
		super();

		this.key=key;
		this.value=value;
		long t=System.currentTimeMillis();
		exptime=t+cache_time;
	}

	public String toString() {
		String out="Cached item:  " + key;
		if(exptime>0) {
			out+="\nExpires:  " + new java.util.Date(exptime);
		}
		out+="\n";
		return(out);
	}

	public Object getObject() {
		return(value);
	}

	public Object getKey() {
		return(key);
	}

	public boolean expired() {
		boolean ret=false;
		if(exptime>0) {
			long t=System.currentTimeMillis();
			ret=(t>exptime);
		}
		return(ret);
	}
}

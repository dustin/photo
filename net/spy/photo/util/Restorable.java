/*
 * Copyright (c) 2000  Dustin Sallings <dustin@spy.net>
 *
 * $Id: Restorable.java,v 1.1 2002/06/28 23:13:09 dustin Exp $
 */

package net.spy.photo.util;

import java.util.*;
import java.io.*;

import org.xml.sax.*;

import net.spy.*;
import net.spy.photo.*;

/**
 * Superclass of all backup entries.
 */
public abstract class Restorable extends Object implements Serializable {

	private String base=null;
	private Hashtable parts=null;

	/**
	 * Get a restorable with the given base.
	 */
	public Restorable(String base) {
		super();
		this.base=base;
		this.parts=new Hashtable();
		// This adds a base handler
		parts.put(base, new StringBuffer());
	}

	/**
	 * Add a handler for the given (relative) path.
	 *
	 * i.e. if the base is /photo_album_object and you pass in keywords it
	 * will add a handler for /photo_album_object/keywords
	 */
	protected void addPartHandler(String path) {
		parts.put(base + "/" + path, new StringBuffer());
	}

	/**
	 * Get the handler for a given path.
	 */
	protected StringBuffer getHandler(String path) throws PhotoException {
		StringBuffer sb=(StringBuffer)parts.get(path);
		if(sb==null) {
			throw new PhotoException("No handler for " + path);
		}

		return(sb);
	}

	/**
	 * Get the content found at a given path.
	 */
	public String getContent(String path) throws PhotoException {
		// Get the handler
		StringBuffer sb=getHandler(path);
		// String it
		return(sb.toString());
	}

	/**
	 * Add some characters for the given path.
	 */
	public void addCharacters(String path, char characters[])
		throws PhotoException {

		// Get the handler
		StringBuffer sb=getHandler(path);

		// Add it
		sb.append(characters);
	}

	/**
	 * String me.
	 */
	public String toString() {
		return("{" + getClass().getName() + " - " + parts + "}");
	}

}

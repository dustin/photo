// Copyright (c) 2000  Dustin Sallings <dustin@spy.net>
// arch-tag: 8B1FC539-5D6E-11D9-9D53-000A957659CC

package net.spy.photo.util;

import java.io.Serializable;
import java.util.HashMap;

import net.spy.photo.PhotoException;

/**
 * Superclass of all backup entries.
 */
public abstract class Restorable extends Object implements Serializable {

	private String base=null;
	private HashMap<String, StringBuffer> parts=null;

	/**
	 * Get a restorable with the given base.
	 */
	public Restorable(String b) {
		super();
		this.base=b;
		this.parts=new HashMap<String, StringBuffer>();
		// This adds a base handler
		parts.put(b, new StringBuffer());
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
		StringBuffer sb=parts.get(path);
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
	@Override
	public String toString() {
		return("{" + getClass().getName() + " - " + parts + "}");
	}

}

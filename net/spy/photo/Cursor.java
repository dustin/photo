/*
 * Copyright (c) 2000  Dustin Sallings <dustin@spy.net>
 *
 * $Id: Cursor.java,v 1.8 2002/07/10 03:38:08 dustin Exp $
 */

package net.spy.photo;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * An object that will be cursor through other objects.
 */
public class Cursor extends ArrayList implements Serializable, Enumeration {

	private int _current=0;
	private int maxret=10;

	/**
	 * Get a search results object for the given URI.
	 */
	public Cursor() {
		super();
	}

	/**
	 * Get a new cursor on the given enumeration.
	 */
	public Cursor(Enumeration e) {
		super();
		while(e.hasMoreElements()) {
			add(e.nextElement());
		}
	}

	/**
	 * Get a new cursor on the given enumeration.
	 */
	public Cursor(Collection c) {
		super(c);
	}

	/**
	 * Set the search result we're lookin' at.
	 */
	public void set(int to) {
		if(to>size()) {
			_current=size();
		} else {
			_current=to;
		}
	}

	/**
	 * Return the iterator from the current position (not the beginning).
	 */
	public Iterator iterator() {
		return(new CursorIterator(this));
	}

	/**
	 * Set the maximum number of results for size.
	 */
	public void setMaxRet(int maxret) {
		this.maxret=maxret;
	}

	/**
	 * Get the requested maximum number of results per page.
	 */
	public int getMaxRet() {
		return(maxret);
	}

	/**
	 * String representation of the search results.
	 */
	public String toString() {
		StringBuffer sb=new StringBuffer();

		sb.append("{Cursor:  pos=");
		sb.append(_current);
		sb.append(", n=");
		sb.append(size());
		sb.append(", maxret=");
		sb.append(maxret);
		sb.append("}");

		return(sb.toString());
	}

	/**
	 * Get the current entry.
	 */
	public Object get() {
		return(get(_current));
	}

	/**
	 * Get the next result, or null if we're done
	 */
	public Object next() {
		Object r=null;

		if(_current<size()) {
			r=get(_current);
			_current++;
		}
		return(r);
	}

	/**
	 * Get the next element.
	 */
	public Object nextElement() {
		Object rv=next();
		if(rv==null) {
			throw new NoSuchElementException("You seek too much.");
		}
		return(rv);
	}

	/**
	 * Return true if there are more elements.
	 */
	public boolean hasMoreElements() {
		return(nRemaining() > 0);
	}

	/**
	 * Get the previous result, or null if we're at the beginning
	 */
	public Object prev() {
		Object r=null;

		if(_current>0) {
			_current--;
			r=get(_current);
		}
		return(r);
	}

	/**
	 * Find out how many results are remaining.
	 */
	public int nRemaining() {
		return(size()-_current);
	}

	/**
	 * Find out which one we're on.
	 */
	public int current() {
		return(_current);
	}

	// Inner class to iterate a cursor
	private class CursorIterator extends Object implements Iterator {

		private Cursor myCursor=null;
	
		// Get a cursor iterator in a given cursor
		private CursorIterator(Cursor c) {
			super();
			this.myCursor=c;
		}

		/**
		 * Get the next object.
		 */
		public Object next() {
			return(myCursor.nextElement());
		}

		/**
		 * True if there are elements remaining.
		 */
		public boolean hasNext() {
			return(myCursor.hasMoreElements());
		}

		/**
		 * Not implemented.
		 */
		public void remove() {
			throw new UnsupportedOperationException("Not implemented.");
		}

	}
}

/*
 * Copyright (c) 2000  Dustin Sallings <dustin@spy.net>
 *
 * $Id: Cursor.java,v 1.12 2003/07/26 08:38:27 dustin Exp $
 */

package net.spy.photo;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.NoSuchElementException;

import net.spy.log.Logger;
import net.spy.log.LoggerFactory;

/**
 * An object that will be cursor through other objects.
 */
public class Cursor extends ArrayList implements Serializable, Enumeration {

	private int current=0;
	private int maxret=10;

	private Logger logger=null;

	/**
	 * Get an empty Cursor object.
	 */
	public Cursor() {
		super();
	}

	/**
	 * Get Cursor object with the specified initial capacity.
	 */
	public Cursor(int capacity) {
		super(capacity);
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
			current=size();
		} else {
			current=to;
		}
	}

	/**
	 * Return the iterator from the current position (not the beginning).
	 */
	public Iterator iterator() {
		return(new CursorIterator());
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
		StringBuffer sb=new StringBuffer(128);

		sb.append("{Cursor:  pos=");
		sb.append(current);
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
		return(get(current));
	}

	/**
	 * Get the next result, or null if we're done
	 */
	public Object next() throws NoSuchElementException {
		Object r=null;

		if(current<size()) {
			r=get(current);
			current++;
		} else {
			throw new NoSuchElementException();
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

		if(current>0) {
			current--;
			r=get(current);
		}
		return(r);
	}

	/** 
	 * Get a Logger instance for this class.
	 */
	protected Logger getLogger() {
		if(logger==null) {
			logger=LoggerFactory.getLogger(getClass());
		}
		return(logger);
	}

	/**
	 * Find out how many results are remaining.
	 */
	public int nRemaining() {
		return(size()-current);
	}

	/**
	 * Find out which one we're on.
	 */
	public int current() {
		return(current);
	}

	// Inner class to iterate a cursor
	private class CursorIterator extends Object implements Iterator {

		// Get a cursor iterator in a given cursor
		private CursorIterator() {
			super();
		}

		/**
		 * Get the next object.
		 */
		public Object next() {
			return(nextElement());
		}

		/**
		 * True if there are elements remaining.
		 */
		public boolean hasNext() {
			return(hasMoreElements());
		}

		/**
		 * Not implemented.
		 */
		public void remove() {
			throw new UnsupportedOperationException("Not implemented.");
		}

	}
}

/*
 * Copyright (c) 2000  Dustin Sallings <dustin@spy.net>
 *
 * $Id: Cursor.java,v 1.2 2002/02/24 01:11:52 dustin Exp $
 */

package net.spy.photo;

import java.util.*;
import java.io.Serializable;

import net.spy.*;

/**
 * An object that will be cursor through other objects.
 */
public class Cursor extends Object implements Serializable {
	private Vector _results=null;
	private int _current=0;
	private int maxret=10;

	/**
	 * Get a search results object for the given URI.
	 */
	public Cursor() {
		super();
		_results=new Vector();
	}

	/**
	 * Get a new cursor on the given enumeration.
	 */
	public Cursor(Enumeration e) {
		this();
		while(e.hasMoreElements()) {
			_results.addElement(e.nextElement());
		}
	}

	/**
	 * Add a search result to the list.
	 */
	public void addElement(Object o) {
		// Now add it
		_results.addElement(o);
	}

	/**
	 * Set the search result we're lookin' at.
	 */
	public void set(int to) {
		if(to>_results.size()) {
			_current=_results.size();
		} else {
			_current=to;
		}
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
		return("Cursor:\n" + _results);
	}

	/**
	 * Get the current entry.
	 */
	public Object get() {
		return(get(_current));
	}

	/**
	 * Get the entry at the given location.
	 */
	public Object get(int which) {
		return(_results.elementAt(which));
	}

	/**
	 * Replace the object at the given position with a new object.
	 */
	protected Object replace(int which, Object o) {
		return(_results.set(which, o));
	}

	/**
	 * Get the next result, or null if we're done
	 */
	public Object next() {
		Object r=null;

		if(_current<_results.size()) {
			r=get(_current);
			_current++;
		}
		return(r);
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
	 * Find out how many results total are in this result set.
	 */
	public int nResults() {
		return(_results.size());
	}

	/**
	 * Find out how many results are remaining.
	 */
	public int nRemaining() {
		return(_results.size()-_current);
	}

	/**
	 * Find out which one we're on.
	 */
	public int current() {
		return(_current);
	}
}

/*
 * Copyright (c) 2000  Dustin Sallings <dustin@spy.net>
 *
 * $Id: PhotoSearchResults.java,v 1.4 2000/07/05 06:08:33 dustin Exp $
 */

package net.spy.photo;

import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;

import net.spy.*;

public class PhotoSearchResults extends Object {
	protected Vector _results=null;
	protected int _current=0;

	public PhotoSearchResults() {
		super();
		_results=new Vector();
	}

	// Add a search result to the list.
	public void add(PhotoSearchResult r) {
		// Set the result id
		r.setId(_results.size());
		_results.addElement(r);
	}

	// Add a photo ID to the result list
	public void add(Integer what) {
		_results.addElement(what);
	}

	// Set the search result we're lookin' at.
	public void set(int to) {
		if(to>_results.size()) {
			_current=_results.size();
		} else {
			_current=to;
		}
	}

	/**
	 * String representation of the search results.
	 */
	public String toString() {
		return("Photo search results:\n" + _results);
	}

	// Get the current entry
	public PhotoSearchResult get() {
		return(get(_current));
	}

	// Get a specific
	public PhotoSearchResult get(int which) {
		PhotoSearchResult ret=null;
		Object o=_results.elementAt(which);
		// We hope that it's a PhotoSearchResult, but an Integer will do.
		try {
			ret=(PhotoSearchResult)o;
		} catch(ClassCastException e) {
			try {
				Integer i=(Integer)o;
				ret=new PhotoSearchResult(i.intValue(), which);
			} catch(Exception e2) {
				System.err.println("Error getting result "
					+ which + ":  " + e2);
			}
		}
		return(ret);
	}

	// Get the next result, or null if we're done
	public PhotoSearchResult next() {
		PhotoSearchResult r=null;

		if(_current<_results.size()) {
			r=get(_current);
			_current++;
		}
		return(r);
	}

	// Get the previous result, or null if we're at the beginning
	public PhotoSearchResult prev() {
		PhotoSearchResult r=null;

		if(_current>0) {
			_current--;
			r=get(_current);
		}
		return(r);
	}

	// Find out how many results total are in this result set
	public int nResults() {
		return(_results.size());
	}

	// Find out how many results are remaining
	public int nRemaining() {
		return(_results.size()-_current);
	}

	// Find out which one we're on
	public int current() {
		return(_current);
	}
}

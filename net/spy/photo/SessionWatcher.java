// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: SessionWatcher.java,v 1.1 2002/06/07 19:51:03 dustin Exp $

package net.spy.photo;

import java.util.*;

import javax.servlet.http.*;

/**
 * Watch session creation and destruction and tally logins and all that.
 */
public class SessionWatcher extends Object implements HttpSessionListener {

	private Vector allSessions=null;

	/**
	 * Get an instance of SessionWatcher.
	 */
	public SessionWatcher() {
		super();
		allSessions=new Vector();
	}

	/**
	 * Called when a session is created.
	 */
	public void sessionCreated(HttpSessionEvent se) {
		allSessions.addElement(se);
	}

	/**
	 * Called when a session is destroyed.
	 */
	public void sessionDestroyed(HttpSessionEvent se) {
		allSessions.removeElement(se);
	}

	/**
	 * Get the total number of sessions currently in this engine.
	 */
	public int totalSessions() {
		return(allSessions.size());
	}

}

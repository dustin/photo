// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: SessionWatcher.java,v 1.2 2002/06/10 03:43:33 dustin Exp $

package net.spy.photo;

import java.util.*;

import javax.servlet.http.*;

/**
 * Watch session creation and destruction and tally logins and all that.
 */
public class SessionWatcher extends Object implements HttpSessionListener {

	private static Vector allSessions=new Vector();

	/**
	 * Get an instance of SessionWatcher.
	 */
	public SessionWatcher() {
		super();
	}

	/**
	 * Called when a session is created.
	 */
	public void sessionCreated(HttpSessionEvent se) {
		synchronized(allSessions) {
			allSessions.addElement(se.getSession());
		}
	}

	/**
	 * Called when a session is destroyed.
	 */
	public void sessionDestroyed(HttpSessionEvent se) {
		synchronized(allSessions) {
			allSessions.removeElement(se.getSession());
		}
	}

	/**
	 * Get the total number of sessions currently in this engine.
	 */
	public static int totalSessions() {
		int rv=0;
		synchronized(allSessions) {
			rv=allSessions.size();
		}
		return(rv);
	}

	/**
	 * Get the SessionData from each session that represents the given
	 * user.
	 */
	public static Enumeration getSessionDataByUser(String username) {
		Vector v=new Vector();

		synchronized(allSessions) {
			for(Enumeration e=allSessions.elements(); e.hasMoreElements(); ) {
				HttpSession session=(HttpSession)e.nextElement();

				if(session.getAttribute("photoSession") != null) {
					PhotoSessionData sessionData=
						(PhotoSessionData)session.getAttribute("photoSession");

					// XXX:  I guess it's theoretically possible for some of
					// this stuff to be null.
					if(sessionData.getUser().getUsername().equals(username)) {
						v.addElement(sessionData);
					} // Correct username
				} // Contains photo session data
			} // Flipping through the sessions
		} // allSession lock

		return(v.elements());
	}

	/**
	 * Return the number of sessions containing the given user.
	 */
	public static int getSessionCountByUser(String username) {
		int rv=0;

		for(Enumeration e=getSessionDataByUser(username);e.hasMoreElements();){
			e.nextElement();
		}

		return(rv);
	}

}

// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: SessionWatcher.java,v 1.10 2002/07/09 21:33:20 dustin Exp $

package net.spy.photo;

import java.util.*;

import javax.servlet.http.*;

import net.spy.photo.*;

/**
 * Watch session creation and destruction and manage sessionData.
 */
public class SessionWatcher extends Object implements HttpSessionListener {

	private static HashSet allSessions=new HashSet();

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
        HttpSession session=se.getSession();

        // Create the session data.
        PhotoSessionData sessionData=new PhotoSessionData();
        // Set the user
        sessionData.setUser(Persistent.getSecurity().getUser("guest"));
        // The rest of the stuff will remain null until something comes
        // along with something better.
        // Now, add it to the session.
        session.setAttribute("photoSession", sessionData);

        // OK, now add that to our list.
		synchronized(allSessions) {
			allSessions.add(se.getSession());
		}
	}

	/**
	 * Called when a session is destroyed.
	 */
	public void sessionDestroyed(HttpSessionEvent se) {
		synchronized(allSessions) {
			allSessions.remove(se.getSession());
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
	public static Collection getSessionDataByUser(String username) {
		ArrayList al=new ArrayList();

		synchronized(allSessions) {
			for(Iterator i=allSessions.iterator(); i.hasNext(); ) {
				HttpSession session=(HttpSession)i.next();

				if(session.getAttribute("photoSession") != null) {
					PhotoSessionData sessionData=
						(PhotoSessionData)session.getAttribute("photoSession");

					// XXX:  I guess it's theoretically possible for some of
					// this stuff to be null.
					if(sessionData.getUser().getUsername().equals(username)) {
						al.add(sessionData);
					} // Found a match
				} else {
					System.err.println(
						"Warning:  Found a session without a photoSession");
				}
			} // Flipping through the sessions
		} // allSession lock

		return(al);
	}

	/**
	 * Return the number of sessions containing the given user.
	 */
	public static int getSessionCountByUser(String username) {
		int rv=0;

		for(Iterator i=getSessionDataByUser(username).iterator();i.hasNext();){
			i.next();
            rv++;
        }

		return(rv);
	}

	/**
	 * Get a Collection of HttpSession objects representing all users
	 * currently in the system.
	 */
	public static Collection listSessions() {
		ArrayList al=new ArrayList();

		synchronized(allSessions) {
			for(Iterator i=allSessions.iterator(); i.hasNext();) {
				HttpSession session=(HttpSession)i.next();

				PhotoSessionData sessionData=
					(PhotoSessionData)session.getAttribute("photoSession");

				if(sessionData!=null) {
					// Add the session to the result List
					al.add(session);
				} else {
					System.err.println(
						"Warning:  Found a session without a photoSession");
				}
			}
		}

		return(al);
	}

}

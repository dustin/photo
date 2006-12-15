// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>

package net.spy.photo.struts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import net.spy.jwebkit.struts.JWebAction;
import net.spy.photo.PhotoSessionData;
import net.spy.photo.User;

/**
 * Superclass for all PhotoAction classes.
 */
public abstract class PhotoAction extends JWebAction {

	public static final String MSGS_ATTRIB = "photo_messages";

	public enum MessageType {
		success, alert, info, failure
	}

	/**
	 * Get an instance of PhotoAction.
	 */
	public PhotoAction() {
		super();
	}

	/**
	 * Get the sessionData from the session.
	 */
	protected PhotoSessionData getSessionData(HttpServletRequest request)
		throws ServletException {

		HttpSession session=request.getSession(false);
		PhotoSessionData sessionData=
			(PhotoSessionData)session.getAttribute(PhotoSessionData.SES_ATTR);

		if(sessionData==null) {
			throw new ServletException("No photoSession in session.");
		}

		return(sessionData);
	}

	/**
	 * Get the current user.
	 */
	protected User getUser(HttpServletRequest request) throws ServletException {
		return(getSessionData(request).getUser());
	}

	@SuppressWarnings("unchecked")
	protected void addMessage(HttpServletRequest req, MessageType type,
			String text) {
		List<Map<String, String>> msgs =
			(List<Map<String, String>>)req.getSession().getAttribute(
					MSGS_ATTRIB);
		if (msgs == null) {
			// Create List in session (this was done automatically)
			msgs = new ArrayList<Map<String, String>>();
			req.getSession().setAttribute(MSGS_ATTRIB, msgs);
		}
		Map<String, String> m=new HashMap<String, String>();
		m.put("type", type.name());
		m.put("text", text);
		msgs.add(m);
	}

}

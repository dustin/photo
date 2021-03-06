// Copyright (c) 2006  Dustin Sallings <dustin@spy.net>

package net.spy.photo.ajax;

import javax.servlet.http.HttpServletRequest;

import net.spy.photo.Persistent;
import net.spy.photo.User;
import net.spy.photo.util.PhotoStorerThread;
import net.spy.xml.SAXAble;

/**
 * Get stats on and control the photo storer thread.
 */
public class PhotoStorerControlServlet extends PhotoAjaxServlet {

	@Override
	protected SAXAble getResults(HttpServletRequest request) throws Exception {
		User user=getUser(request);
		if(!user.isInRole(User.ADMIN)) {
			throw new Exception("Unauthorized");
		}
		PhotoStorerThread rv=Persistent.getStorerThread();
		if(request.getPathInfo().equals("/start")) {
			getLogger().info("%s requesting a manual flush", user);
			rv.performFlush();
		}
		return(rv);
	}

}

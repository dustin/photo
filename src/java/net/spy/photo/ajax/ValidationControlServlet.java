// Copyright (c) 2005  Dustin Sallings <dustin@spy.net>
// arch-tag: 93C7F819-FF99-4353-A895-BB1DEEFF3975

package net.spy.photo.ajax;

import javax.servlet.http.HttpServletRequest;

import net.spy.photo.CacheValidator;
import net.spy.photo.User;
import net.spy.xml.SAXAble;

/**
 * Control and get status on cache validation.
 */
public class ValidationControlServlet extends PhotoAjaxServlet {

	@Override
	protected SAXAble getResults(HttpServletRequest request) throws Exception {
		User user=getUser(request);
		if(!user.isInRole(User.ADMIN)) {
			throw new Exception("Unauthorized");
		}
		CacheValidator rv=CacheValidator.getInstance();
		if(request.getPathInfo().equals("/start")) {
			getLogger().info("Starting validation process");
			rv.process(user);
		} else if(request.getPathInfo().equals("/cancel")) {
			getLogger().info("Cancelling validation process");
			rv.cancelProcessing();
		}
		return(rv);
	}

}

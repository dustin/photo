package net.spy.photo.aspects;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.spy.SpyObject;
import net.spy.photo.rest.BaseRestServlet;

/**
 * Aspect to destroy sessions after a REST call is made.
 */
public aspect RestSessionDestroyAspect extends SpyObject {
	pointcut didCall(HttpServletRequest req, HttpServletResponse res):
		args(req, res)
			&& execution(void BaseRestServlet+.do*(
					HttpServletRequest, HttpServletResponse));

	after(HttpServletRequest req, HttpServletResponse res) returning:
		didCall(req, res) {
		HttpSession ses=req.getSession(false);
		if(ses != null) {
			getLogger().info("Invalidating session after rest request: "
					+ thisJoinPointStaticPart.toLongString());
			ses.invalidate();
		}
	}
}

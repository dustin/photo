// Copyright (c) 2004  Dustin Sallings <dustin@spy.net>

package net.spy.photo.struts;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * Logout the user (and invalidate any persistent session).
 */
public class LogoutAction extends net.spy.jwebkit.struts.LogoutAction {

	/**
	 * Call super's spyExecute after clearing out the persess cookie.
	 */
	@Override
	protected ActionForward spyExecute(ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request, HttpServletResponse response)
		throws Exception {

		Cookie c=new Cookie(LoginAction.PERSESS_COOKIE, "delete");
		c.setMaxAge(0); // delete
		response.addCookie(c);

		return(super.spyExecute(mapping, form, request, response));
	}

}

// Copyright (c) 2006  Dustin Sallings <dustin@spy.net>
// arch-tag: 03657D4B-35CB-4D65-92D2-9E31A7AE1EC4

package net.spy.photo;

import java.security.Principal;

import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.spy.SpyObject;
import net.spy.jwebkit.auth.PasswordCheckingAuthAdaptor;

/**
 * jwebkit AuthAdaptor for authenticating photo users.
 */
public class PhotoAuthAdaptor extends SpyObject
	implements PasswordCheckingAuthAdaptor {

	public void init(FilterConfig c) throws ServletException {
		getLogger().info("Initializing");
	}

	public boolean isPrincipalInRole(HttpServletRequest req,
			HttpServletResponse res, ServletContext ctx, Principal p,
			String role) {
		User u=(User)p;
		return u.isInRole(role);
	}

	public Principal checkPassword(String u, String p) throws ServletException {
		User rv=null;
        try {
            UserFactory uf=UserFactory.getInstance();
            User tmp=uf.getUser(u);
            PhotoSecurity sec=Persistent.getSecurity();
            if(sec.checkPassword(p, tmp.getPassword())) {
                rv=tmp;
            }
            getLogger().info("Logged in " + rv);
        } catch(Exception e) {
            getLogger().info("Problem logging in user", e);
        }

		return rv;
	}

	public void didAuthenticate(Principal p,
			HttpServletRequest req, HttpServletResponse res) {
        HttpSession session=req.getSession(true);
        PhotoSessionData psd=(PhotoSessionData)session.getAttribute(
            PhotoSessionData.SES_ATTR);
        assert psd != null : "no PhotoSessionData";
        psd.setUser((User)p);
	}

}

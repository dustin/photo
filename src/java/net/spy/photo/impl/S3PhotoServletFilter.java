package net.spy.photo.impl;

import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.spy.SpyObject;
import net.spy.photo.Persistent;
import net.spy.photo.PhotoDimensions;
import net.spy.photo.PhotoImage;
import net.spy.photo.PhotoServletChain;
import net.spy.photo.S3Service;
import net.spy.photo.User;

public class S3PhotoServletFilter extends BasePhotoServletFilter {

	public void doFilter(PhotoImage pid, User u, PhotoDimensions dims,
			HttpServletRequest req, HttpServletResponse res,
			PhotoServletChain chain) throws Exception {

		boolean local = req.getRemoteAddr().startsWith("192.168.");

		S3Service s3s = S3Service.getInstance();
		if (!local && s3s.contains(pid.getId(), dims, pid.getFormat())) {
			String url = s3s.makeURL(pid.getId(), dims, pid.getFormat());
			getLogger().debug("Redirecting to %s", url);
			res.sendRedirect(url);
		} else {
			if (!local && dims != null && s3s.isFunctional()) {
				Persistent.getExecutor().schedule(new Cacher(pid, dims),
					5, TimeUnit.SECONDS);
			}
			chain.doChain(pid, u, dims, req, res);
		}
	}

	// This is really just a little lambda class, except for the exception
	// handling and then having to send some more data in because it's not
	// a closure like it used to be and stuff like that.
	private static class Cacher extends SpyObject implements Runnable {
		private PhotoImage pid = null;
		private PhotoDimensions dims = null;

		public Cacher(PhotoImage p, PhotoDimensions d) {
			super();
			pid = p;
			dims = d;
		}
		public void run() {
			try {
				S3Service.getInstance().send(pid, dims);
			} catch (Exception e) {
				getLogger().warn("Problem sending missing image", e);
			}
		}
	}
}

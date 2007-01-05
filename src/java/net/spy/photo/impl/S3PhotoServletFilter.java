package net.spy.photo.impl;

import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.spy.photo.Persistent;
import net.spy.photo.PhotoDimensions;
import net.spy.photo.PhotoImageData;
import net.spy.photo.PhotoServletChain;
import net.spy.photo.S3Service;
import net.spy.photo.User;

public class S3PhotoServletFilter extends BasePhotoServletFilter {

	public void doFilter(final PhotoImageData pid, User u,
			final PhotoDimensions dims,
			HttpServletRequest req, HttpServletResponse res,
			PhotoServletChain chain) throws Exception {

		boolean local=req.getRemoteAddr().startsWith("192.168.");

		final S3Service s3s=S3Service.getInstance();
		if(!local && s3s.contains(pid.getId(), dims, pid.getFormat())) {
			String url=s3s.makeURL(pid.getId(), dims, pid.getFormat());
			getLogger().debug("Redirecting to %s", url);
			res.sendRedirect(url);
		} else {
			if(!local && dims != null && s3s.isFunctional()) {
				Persistent.getExecutor().schedule(new Runnable() {
					public void run() {
						try {
							s3s.send(pid, dims);
						} catch(Exception e) {
							getLogger().warn("Problem sending missing image",
									e);
						}}},
						5, TimeUnit.SECONDS);
			}
			chain.doChain(pid, u, dims, req, res);
		}
	}

}

package net.spy.photo.impl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.spy.photo.PhotoDimensions;
import net.spy.photo.PhotoImageData;
import net.spy.photo.PhotoServletChain;
import net.spy.photo.S3Service;
import net.spy.photo.User;

public class S3PhotoServletFilter extends BasePhotoServletFilter {

	public void doFilter(PhotoImageData pid, User u, PhotoDimensions dims,
			HttpServletRequest req, HttpServletResponse res,
			PhotoServletChain chain) throws Exception {

		boolean local=req.getRemoteAddr().startsWith("192.168.");

		S3Service s3s=S3Service.getInstance();
		if(!local && s3s.contains(pid.getId(), dims, pid.getFormat())) {
			String url=s3s.makeURL(pid.getId(), dims, pid.getFormat());
			getLogger().debug("Redirecting to %s", url);
			res.sendRedirect(url);
		} else {
			chain.doChain(pid, u, dims, req, res);
		}
	}

}

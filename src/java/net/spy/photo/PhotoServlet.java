// Copyright (c) 1999 Dustin Sallings

package net.spy.photo;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.spy.jwebkit.JWHttpServlet;
import net.spy.photo.impl.BasePhotoServletFilter;
import net.spy.photo.impl.PhotoDimensionsImpl;
import net.spy.photo.impl.S3PhotoServletFilter;
import net.spy.stat.Stats;
import net.spy.util.CloseUtil;

/**
 * Serve up images.
 */
public class PhotoServlet extends JWHttpServlet {

	// Do the work.
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {

		String size = null;
		long start = System.currentTimeMillis();

		// Get the sessionData
		HttpSession ses = req.getSession(false);
		PhotoSessionData sessionData = (PhotoSessionData) ses
				.getAttribute(PhotoSessionData.SES_ATTR);
		assert sessionData != null : "Session data went null";

		String stmp = req.getParameter("id");
		if (stmp == null) {
			// If the above failed, try ``photo_id'' (for backwards
			// compatibility).
			stmp = req.getParameter("photo_id");
			if (stmp == null) {
				throw new ServletException("id required");
			}
		}
		// Parse the string.
		int imgId = Integer.parseInt(stmp);

		// Figure out if they want a thumbnail or what.
		stmp = req.getParameter("thumbnail");
		if (stmp != null) {
			size = PhotoConfig.getInstance().get("thumbnail_size");
		}
		stmp = req.getParameter("scale");
		if (stmp != null) {
			size = stmp;
		}
		// Get the dimensions for scaling
		PhotoDimensions pdim = null;
		if (size != null) {
			pdim = new PhotoDimensionsImpl(size);
		}

		// Figure out if we should display the image or error
		PhotoImageFactory pidf = PhotoImageFactory.getInstance();
		try {
			PhotoImage pid = pidf.getObject(imgId);

			if (!Persistent.getSecurity().testAccess(sessionData.getUser(),
					imgId)) {
				res.setStatus(HttpServletResponse.SC_FORBIDDEN);
				sendPlain("Image " + imgId + " cannot be viewed by "
						+ sessionData.getUser(), res);
			} else {
				try {
					buildChain().doChain(pid, sessionData.getUser(), pdim, req,
							res);
				} catch (Exception e) {
					throw new ServletException("Error serving image", e);
				}
				// Mark it in the session
				sessionData.sawImage(imgId);
				long end = System.currentTimeMillis();
				Stats.getComputingStat("img.all").add(end - start);
				Stats.getComputingStat(
						"img.size." + (pdim == null ? "full" : pdim)).add(
						end - start);
			}
		} catch (PhotoImageFactory.NoSuchPhotoException e) {
			getLogger().warn(
					sessionData.getUser() + " requested missing image", e);
			res.setStatus(HttpServletResponse.SC_NOT_FOUND);
			sendPlain("Image " + imgId + " cannot be found", res);
		}

	}

	private PhotoServletChain buildChain() {
		Collection<PhotoServletFilter> filters=
			new ArrayList<PhotoServletFilter>();
		filters.add(new S3PhotoServletFilter());
		filters.add(new ImgWriter());
		return new PhotoServletChain(filters);
	}

	static class ImgWriter extends BasePhotoServletFilter {

		public void doFilter(PhotoImage pid, User u, PhotoDimensions dims,
				HttpServletRequest req, HttpServletResponse res,
				PhotoServletChain chain) throws Exception {

			int imgId = pid.getId();

			PhotoImageHelper p = PhotoImageHelper.getInstance();
			getLogger().info("Fetching %d scaled to %s for %s", imgId, dims, u);
			byte[] image = p.getImage(u, pid, dims);

			res.setContentType(pid.getFormat().getMime());
			res.setContentLength(image.length);
			// Setup cache
			res.addHeader("Cache-Control", "private, max-age=86400");
			OutputStream os = res.getOutputStream();
			try {
				os.write(image);
			} finally {
				CloseUtil.close(os);
			}

			logAccess(u, imgId, dims, req);

			chain.doChain(pid, u, dims, req, res);
		}
	}
}

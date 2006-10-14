// Copyright (c) 1999 Dustin Sallings
// arch-tag: 40908250-5D6D-11D9-AE41-000A957659CC

package net.spy.photo;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.spy.jwebkit.JWHttpServlet;
import net.spy.photo.impl.PhotoDimensionsImpl;
import net.spy.photo.log.PhotoLogImageEntry;
import net.spy.stat.Stats;
import net.spy.util.CloseUtil;

/**
 * Serve up images.
 */
public class PhotoServlet extends JWHttpServlet { 

	// Do the work.
	protected void doGetOrPost(HttpServletRequest req,
		HttpServletResponse res) throws ServletException, IOException {

		String size=null;
		long start=System.currentTimeMillis();

		// Get the sessionData
		HttpSession ses=req.getSession(false);
		PhotoSessionData sessionData=(PhotoSessionData)ses.getAttribute(
			PhotoSessionData.SES_ATTR);
		assert sessionData != null : "Session data went null";

		String stmp=req.getParameter("id");
		if(stmp==null) {
			// If the above failed, try ``photo_id'' (for backwards
			// compatibility).
			stmp=req.getParameter("photo_id");
			if(stmp==null) {
				throw new ServletException("id required");
			}
		}
		// Parse the string.
		int imgId=Integer.parseInt(stmp);

		// Figure out if they want a thumbnail or what.
		stmp=req.getParameter("thumbnail");
		if(stmp!=null) {
			size=PhotoConfig.getInstance().get("thumbnail_size");
		}
		stmp=req.getParameter("scale");
		if(stmp!=null) {
			size=stmp;
		}
		// Get the dimensions for scaling
		PhotoDimensions pdim=null;
		if(size!=null) {
			pdim=new PhotoDimensionsImpl(size);
		}

		// Figure out if we should display the image or error
		PhotoImageDataFactory pidf=PhotoImageDataFactory.getInstance();
		try {
			pidf.getObject(imgId);

			if(!Persistent.getSecurity().testAccess(sessionData.getUser(),
				imgId)) {
				res.setStatus(HttpServletResponse.SC_FORBIDDEN);
				sendPlain("Image " + imgId + " cannot be viewed by "
					+ sessionData.getUser(), res);
			} else {
				try {
					writeImage(sessionData.getUser(), imgId, pdim, req, res);
				} catch(Exception e) {
					throw new ServletException("Error serving image", e);
				}
				// Mark it in the session
				sessionData.sawImage(imgId);
				long end=System.currentTimeMillis();
				Stats.getComputingStat("img.all").add(end-start);
				Stats.getComputingStat("img.size." + (pdim==null?"full":pdim))
					.add(end-start);
			}
		} catch(PhotoImageDataFactory.NoSuchPhotoException e) {
			getLogger().warn(sessionData.getUser()
				+ " requested missing image", e);
			res.setStatus(HttpServletResponse.SC_NOT_FOUND);
			sendPlain("Image " + imgId + " cannot be found", res);
		}

	}

	private void writeImage(User u, int imgId, PhotoDimensions pdim,
		HttpServletRequest req, HttpServletResponse res) throws
		Exception {

		PhotoImageHelper p=new PhotoImageHelper(imgId);
		log("Fetching " + imgId + " scaled to " + pdim + " for " + u);
		PhotoImage image=p.getImage(u, pdim);

		res.setContentType(image.getFormat().getMime());
		res.setContentLength(image.size());
		OutputStream os=res.getOutputStream();
		try {
			os.write(image.getData());
		} finally {
			CloseUtil.close(os);
		}

		// Log it
		Persistent.getPipeline().addTransaction(new PhotoLogImageEntry(
			u.getId(), imgId, pdim, req), PhotoConfig.getInstance());
	}
}

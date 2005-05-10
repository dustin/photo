// Copyright (c) 1999 Dustin Sallings
// arch-tag: 40908250-5D6D-11D9-AE41-000A957659CC

package net.spy.photo;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.spy.photo.impl.PhotoDimensionsImpl;
import net.spy.photo.log.PhotoLogImageEntry;

/**
 * Serve up images.
 */
public class PhotoServlet extends HttpServlet { 

	/**
	 * Initialize the servlet.
	 */
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
	}

	/**
	 * Shut down the servlet.
	 */
	public void destroy() {
		log("Calling super destroy.");
		super.destroy();
	}

	/**
	 * Servlet info.
	 */
	public String getServletInfo() {
		return("Copyright (c) 2000  Dustin Sallings <dustin@spy.net>"
			+ " - $Revision: 1.27 $");
	}

	/**
	 * Process GET request.
	 */
	protected void doGet (
		HttpServletRequest request, HttpServletResponse response
	) throws ServletException, IOException {
		process(request, response);
	}

	/**
	 * Process POST requests.
	 */
	protected void doPost (
		HttpServletRequest request, HttpServletResponse response
	) throws ServletException, IOException {
		process(request, response);
	}

	// Do the work.
	private void process(HttpServletRequest request,
		HttpServletResponse response) throws ServletException, IOException {

		int which=-1;
		String size=null;
		ServletOutputStream out=null;
		PhotoConfig conf=PhotoConfig.getInstance();

		// Get the sessionData
		HttpSession session=request.getSession(false);
		User pu=null;
		PhotoSessionData sessionData=null;
		if(session==null) {
			log("Session is null, using the default user");
			pu=Persistent.getSecurity().getDefaultUser();
		} else {
			sessionData=(PhotoSessionData)
				session.getAttribute(PhotoSessionData.SES_ATTR);
			if(sessionData!=null) {
				pu=sessionData.getUser();
			} else {
				log("session data is null, using the default user");
				pu=Persistent.getSecurity().getDefaultUser();
			}
		}

		// There should be one by now
		if(pu == null) {
			throw new ServletException("Couldn't find a photo user anywhere");
		}

		String stmp=request.getParameter("id");
		if(stmp==null) {
			// If the above failed, try ``photo_id'' (for backwards
			// compatibility).
			stmp=request.getParameter("photo_id");
			if(stmp==null) {
				throw new ServletException("id required");
			}
		}
		// Parse the string.
		which=Integer.parseInt(stmp);

		// Figure out if they want a thumbnail or what.
		stmp=request.getParameter("thumbnail");
		if(stmp!=null) {
			size=conf.get("thumbnail_size");
		}
		stmp=request.getParameter("scale");
		if(stmp!=null) {
			size=stmp;
		}

		// OK, at this point, we're ready to start serving up the image

		try {
			// Get the image API
			PhotoImageHelper p=new PhotoImageHelper(which);

			// Get the output stream
			out=response.getOutputStream();

			// Get the dimensions for scaling
			PhotoDimensions pdim=null;
			if(size!=null) {
				pdim=new PhotoDimensionsImpl(size);
			}

			log("Fetching " + which + " scaled to " + pdim + " for " + pu);
			PhotoImage image=p.getImage(pu, pdim);

			// Log it
			Persistent.getPipeline().addTransaction(new PhotoLogImageEntry(
				pu.getId(), which, pdim, request), PhotoConfig.getInstance());

			// Tell the client what we're sending
			response.setContentType(image.getFormat().getMime());

			// Tell the client the size
			response.setContentLength(image.size());

			// Send it
			out.write(image.getData());

			// Mark it in the session
			if(sessionData != null) {
				sessionData.sawImage(which);
			}

		} catch(Exception e) {
			throw new ServletException("Error displaying image", e);
		}
	}
}

/*
 * Copyright (c) 1999 Dustin Sallings
 *
 * $Id: PhotoServlet.java,v 1.24 2002/07/01 18:03:19 dustin Exp $
 */

package net.spy.photo;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;

import net.spy.*;

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
			+ " - $Revision: 1.24 $");
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
		PhotoConfig conf=new PhotoConfig();

		// Get the sessionData
		HttpSession session=request.getSession(false);
		if(session==null) {
			throw new ServletException("No session!");
		}
		PhotoSessionData sessionData=
			(PhotoSessionData)session.getAttribute("photoSession");
		if(sessionData==null) {
			throw new ServletException("No sesion data in session");
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

			log("Fetching " + which + " scaled to " + pdim);
			PhotoImage image=p.getImage(sessionData.getUser(), pdim);

			// Log it
			Persistent.getLogger().log(new PhotoLogImageEntry(
				sessionData.getUser().getId(), which, pdim, request));

			// Tell the client what we're sending
			response.setContentType("image/" + image.getFormatString());

			// Tell the client the size
			response.setContentLength(image.size());

			// Send it
			out.write(image.getData());

			// Mark it in the session
			sessionData.sawImage(which);

		} catch(Exception e) {
			throw new ServletException("Error displaying image", e);
		}
	}
}

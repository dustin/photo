/*
 * Copyright (c) 1999 Dustin Sallings
 *
 * $Id: PhotoServlet.java,v 1.22 2002/05/05 08:46:20 dustin Exp $
 */

package net.spy.photo;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;

import net.spy.*;

// The class
public class PhotoServlet extends HttpServlet
{ 
	// The once only init thingy.
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
	}

	// Shut the servlet down.
	public void destroy() {
		log("Calling super destroy.");
		super.destroy();
	}

	// Servlet info
	public String getServletInfo() {
		return("Copyright (c) 2000  Dustin Sallings <dustin@spy.net>"
			+ " - $Revision: 1.22 $");
	}

	// Do a GET request
	public void doGet (
		HttpServletRequest request, HttpServletResponse response
	) throws ServletException, IOException {
		PhotoSession ps = new PhotoSession(this, request, response);
		ps.process();
	}

	// Do a POST request
	public void doPost (
		HttpServletRequest request, HttpServletResponse response
	) throws ServletException, IOException {

		PhotoSession ps = new PhotoSession(this, request, response);
		ps.process();
	}
}

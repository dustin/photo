/*
 * Copyright (c) 1999 Dustin Sallings
 *
 * $Id: XMLRPCServlet.java,v 1.1 2002/06/25 00:18:01 dustin Exp $
 */

package net.spy.photo;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.apache.xmlrpc.*;

import net.spy.photo.rpc.*;

/**
 * Serve up images.
 */
public class XMLRPCServlet extends HttpServlet { 

	private XmlRpcServer xmlrpc=null;

	/**
	 * Initialize the servlet.
	 */
	public void init(ServletConfig config) throws ServletException {
		super.init(config);

		log("Initializing XMLRPC services.");
		xmlrpc=new XmlRpcServer();

		xmlrpc.addHandler("addImage", new AddImage());
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
		return("Copyright (c) 2000	Dustin Sallings <dustin@spy.net>"
			+ " - $Revision: 1.1 $");
	}

	/**
	 * Process POST requests.
	 */
	protected void doPost (
		HttpServletRequest request, HttpServletResponse response
	) throws ServletException, IOException {

		// Do the work
		byte[] result=xmlrpc.execute(request.getInputStream());
		response.setContentType("text/xml");
		response.setContentLength(result.length);
		OutputStream os=response.getOutputStream();
		os.write(result);
		os.flush();
		os.close();
	}
}

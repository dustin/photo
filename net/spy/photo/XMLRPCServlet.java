/*
 * Copyright (c) 1999 Dustin Sallings
 *
 * $Id: XMLRPCServlet.java,v 1.2 2002/07/10 03:38:08 dustin Exp $
 */

package net.spy.photo;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.spy.photo.rpc.AddImage;

import org.apache.xmlrpc.XmlRpcServer;

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
			+ " - $Revision: 1.2 $");
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

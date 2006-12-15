// Copyright (c) 2006  Dustin Sallings <dustin@spy.net>

package net.spy.photo.rest;

import java.io.IOException;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.spy.jwebkit.xml.XMLOutputServlet;
import net.spy.photo.PhotoUtil;

/**
 * Base class for all REST servlets.
 */
public abstract class BaseRestServlet extends XMLOutputServlet {

	@Override
	protected void doGetOrPost(HttpServletRequest req, HttpServletResponse res)
		throws ServletException, IOException {
		throw new ServletException("Not implemented");
	}

	protected int getIntParameter(HttpServletRequest req, String param) {
		return Integer.parseInt(getStringParameter(req, param));
	}

	protected String getStringParameter(HttpServletRequest req, String param) {
		String rv=req.getParameter(param);
		if(rv == null) {
			throw new IllegalArgumentException("Missing parameter:  " + param);
		}
		return rv;
	}

	protected Date getDateParameter(HttpServletRequest req, String param) {
		String dateString=getStringParameter(req, param);
		return PhotoUtil.parseDate(dateString);
	}
}

// Copyright (c) 2006  Dustin Sallings <dustin@spy.net>
// arch-tag: CB6035A5-119E-43AC-A1F5-FE2D55BEF5BA

package net.spy.photo.rest;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.xml.sax.ContentHandler;

import net.spy.jwebkit.xml.XMLUtils;
import net.spy.photo.Category;
import net.spy.photo.CategoryFactory;
import net.spy.photo.User;

/**
 * Get the available categories for the current user.
 */
public class GetCategories extends BaseRestServlet {

	protected void doGet(HttpServletRequest req, HttpServletResponse res)
		throws ServletException, IOException {

		User u = (User)req.getUserPrincipal();
		assert u != null : "No current user";

		try {
			CategoryFactory cf = CategoryFactory.getInstance();
			Collection<Category> cats = cf.getCatList(u.getId(),
					CategoryFactory.ACCESS_WRITE);
			XMLUtils x = XMLUtils.getInstance();
			ContentHandler ch = getContentHandler(res);
			ch.startDocument();
			x.startElement(ch, "categories");
			for (Category c : cats) {
				x.doElement(ch, "cat", c.getName(), Collections.singletonMap(
						"id", String.valueOf(c.getId())));
			}
			x.endElement(ch, "categories");
			ch.endDocument();
		} catch (Exception e) {
			throw new ServletException("Problem getting categories", e);
		}		
	}

}

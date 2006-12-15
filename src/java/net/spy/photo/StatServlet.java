// Copyright (c) 2006  Dustin Sallings <dustin@spy.net>

package net.spy.photo;

import java.io.IOException;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.spy.jwebkit.JWHttpServlet;
import net.spy.stat.Stat;
import net.spy.stat.Stats;

/**
 * Servlet up the stats.
 */
public class StatServlet extends JWHttpServlet {

	@Override
	protected void doGetOrPost(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		SortedMap<String, Stat> stuff=new TreeMap<String, Stat>(
				Stats.getInstance().getStats());
		res.setContentType("text/plain");
		ServletOutputStream sos=res.getOutputStream();
		for(Stat s : stuff.values()) {
			sos.println(String.valueOf(s));
		}
		sos.close();
	}

}

// Copyright (c) 2005  Dustin Sallings <dustin@spy.net>

package net.spy.photo.ajax;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import net.spy.photo.Category;
import net.spy.photo.CategoryFactory;
import net.spy.xml.MapElement;
import net.spy.xml.SAXAble;

/**
 * Get the categories available to the current user.
 */
public class Categories extends PhotoAjaxServlet {

	private Map<String, Integer> types=null;

	@Override
	public void init(ServletConfig conf) throws ServletException {
		super.init(conf);
		types=new HashMap<String, Integer>();
		types.put("/",
			CategoryFactory.ACCESS_WRITE|CategoryFactory.ACCESS_READ);
		types.put("/write", CategoryFactory.ACCESS_WRITE);
		types.put("/read", CategoryFactory.ACCESS_READ);
	}
	
	@Override
	protected SAXAble getResults(HttpServletRequest request) throws Exception {
		String pathInfo=request.getPathInfo();
		int access=types.get(pathInfo);

		CategoryFactory cf=CategoryFactory.getInstance();

		Map<String, Object> rv=new TreeMap<String, Object>();
		for(Category cat : cf.getCatList(getUser(request).getId(), access)) {
			rv.put(cat.getName(), cat.getId());
		}

		return(new MapElement("cats", "cat", rv));
	}
}

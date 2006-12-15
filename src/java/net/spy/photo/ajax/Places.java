// Copyright (c) 2006  Dustin Sallings <dustin@spy.net>

package net.spy.photo.ajax;

import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;

import net.spy.photo.Place;
import net.spy.photo.PlaceFactory;
import net.spy.xml.MapElement;
import net.spy.xml.SAXAble;

/**
 * Get the known places.
 */
public class Places extends PhotoAjaxServlet {

	@Override
	protected SAXAble getResults(HttpServletRequest request) throws Exception {
		Map<String, Object> rv=new TreeMap<String, Object>();

		for(Place p : PlaceFactory.getInstance().getObjects()) {
			rv.put(p.getName(), p.getId());
		}

		return(new MapElement("places", "place", rv));
	}

}

// Copyright (c) 2006  Dustin Sallings <dustin@spy.net>

package net.spy.photo.ajax;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.spy.SpyObject;
import net.spy.photo.PhotoImageData;
import net.spy.photo.PhotoProperties;
import net.spy.photo.search.ParallelSearch;
import net.spy.photo.struts.SearchForm;
import net.spy.xml.CollectionElement;
import net.spy.xml.SAXAble;
import net.spy.xml.XMLUtils;

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

/**
 * Get some images for a slideshow for the current user.
 */
public class SlideshowServlet extends PhotoAjaxServlet {

	private static final int MAX_IMAGES=50;

	@Override
	protected SAXAble getResults(HttpServletRequest request) throws Exception {
		List<SAXAble> images=new ArrayList<SAXAble>();

		SearchForm sf=new SearchForm();
		sf.setField("keywords");
		PhotoProperties props=PhotoProperties.getInstance();
		sf.setWhat(props.getProperty("idxkeywords", ""));

		ParallelSearch s=ParallelSearch.getInstance();
		for(PhotoImageData pid :
			s.performSearch(sf, getUser(request), null).getAllObjects()) {

			images.add(new SAXAbleImage(pid));
		}

		Collections.shuffle(images);
		if(images.size() > MAX_IMAGES) {
			images=images.subList(0, MAX_IMAGES);
		}

		return new CollectionElement("imgs", images);
	}

	private static class SAXAbleImage extends SpyObject implements SAXAble {

		private int id=0;
		private String descr=null;

		public SAXAbleImage(PhotoImageData pid) {
			super();
			id=pid.getId();
			descr=pid.getDescr();
		}

		public void writeXml(ContentHandler h) throws SAXException {
			XMLUtils xu=XMLUtils.getInstance();
			xu.startElement(h, "img",
					Collections.singletonMap("id", String.valueOf(id)));
			xu.doElement(h, "descr", descr);
			xu.endElement(h, "img");
		}

	}
}

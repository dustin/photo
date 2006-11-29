// Copyright (c) 2004  Dustin Sallings <dustin@spy.net>
// arch-tag: CFF3FF42-5D6C-11D9-8B1E-000A957659CC

package net.spy.photo;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import net.spy.jwebkit.xml.XMLOutputServlet;
import net.spy.photo.search.ParallelSearch;
import net.spy.photo.search.SearchResults;
import net.spy.photo.struts.SearchForm;
import net.spy.xml.SAXAble;
import net.spy.xml.XMLUtils;

/**
 * Export data from the photo album.
 */
public class ExportServlet extends XMLOutputServlet {

	/**
	 * Get an instance of ExportServlet.
	 */
	public ExportServlet() {
		super();
	}

	/** 
	 * Process the request.
	 */
	@Override
	protected void doGetOrPost(HttpServletRequest req, HttpServletResponse res)
		throws ServletException, IOException {

		try {
			HttpSession ses=req.getSession(false);
			PhotoSessionData sessionData=(PhotoSessionData)ses.getAttribute(
				PhotoSessionData.SES_ATTR);

			SearchForm sf=new SearchForm();
			sf.setSdirection("desc");
			ParallelSearch ps=ParallelSearch.getInstance();
			SearchResults psr=ps.performSearch(sf, sessionData.getUser());

			sendXml(new SearchResultsXML(psr), res);
		} catch(Exception e) {
			throw new ServletException("Problem exporting data", e);
		}
	}

	private static class SearchResultsXML implements SAXAble {

		private SearchResults results=null;
		private SimpleDateFormat dateFormat=null;
		private SimpleDateFormat tsFormat=null;

		public SearchResultsXML(SearchResults r) {
			super();
			results=r;
			dateFormat=new SimpleDateFormat("yyyy-MM-dd");
			tsFormat=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		}

		public void writeXml(ContentHandler h) throws SAXException {
			XMLUtils x=XMLUtils.getInstance();
			x.startElement(h, "photoexport");
			x.startElement(h, "album");
			for(PhotoImageData pid : results) {
				x.startElement(h, "photo");
				x.doElement(h, "id", String.valueOf(pid.getId()));
				x.doElement(h, "addedby", pid.getAddedBy().getName());
				x.doElement(h, "cat", pid.getCatName());
				x.doElement(h, "taken", dateFormat.format(pid.getTaken()));
				x.doElement(h, "ts", tsFormat.format(pid.getTimestamp()));
				x.doElement(h, "descr", pid.getDescr());
				x.doElement(h, "size", String.valueOf(pid.getSize()));
				x.doElement(h, "width",
						String.valueOf(pid.getDimensions().getWidth()));
				x.doElement(h, "height",
						String.valueOf(pid.getDimensions().getHeight()));
				x.doElement(h, "tnwidth",
						String.valueOf(pid.getTnDims().getWidth()));
				x.doElement(h, "tnheight",
						String.valueOf(pid.getTnDims().getHeight()));
				x.doElement(h, "extension", pid.getFormat().getExtension());

				// Do the collections things at the end
				x.startElement(h, "keywords");
				for(Keyword k : pid.getKeywords()) {
					Map<String, String> m=new HashMap<String, String>();
					m.put("word", k.getKeyword());
					x.doElement(h, "keyword", null, m);
				}
				x.endElement(h, "keywords");

				// Annotations
				if(pid.getAnnotations().size() > 0) {
					x.startElement(h, "annotations");
					for(AnnotatedRegion ar : pid.getAnnotations()) {
						x.startElement(h, "annotation");

						x.doElement(h, "x", String.valueOf(ar.getX()));
						x.doElement(h, "y", String.valueOf(ar.getY()));
						x.doElement(h, "width", String.valueOf(ar.getWidth()));
						x.doElement(h, "height",
							String.valueOf(ar.getHeight()));
						x.doElement(h, "title", ar.getTitle());
						x.doElement(h, "addedby", ar.getUser().getName());
						x.doElement(h, "ts",
							tsFormat.format(ar.getTimestamp()));

						x.startElement(h, "keywords");
						for(Keyword k : ar.getKeywords()) {
							Map<String, String> m=new HashMap<String, String>();
							m.put("word", k.getKeyword());
							x.doElement(h, "keyword", null, m);
						}
						x.endElement(h, "keywords");

						x.endElement(h, "annotation");
					}
					x.endElement(h, "annotations");
				} // end annotations

				x.endElement(h, "photo");
			}
			x.endElement(h, "album");
			x.endElement(h, "photoexport");
		}
		
	}
}

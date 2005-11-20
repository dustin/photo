// Copyright (c) 2005  Dustin Sallings <dustin@spy.net>
// arch-tag: 5CA2AB01-D093-45A1-A803-5CDC061F61BA

package net.spy.photo;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.spy.jwebkit.rss.RSSChannel;
import net.spy.jwebkit.rss.RSSItem;
import net.spy.photo.ajax.PhotoAjaxServlet;
import net.spy.photo.search.Search;
import net.spy.photo.search.SearchResults;
import net.spy.photo.struts.SearchForm;

import org.xml.sax.ContentHandler;

/**
 * Servlet for sending RSS data from search results.
 */
public class RSSServlet extends PhotoAjaxServlet {

	private static final int CHANNEL_SIZE=25;

	/** 
	 * Handle the RSS request.
	 */
	protected void processRequest(
		HttpServletRequest req, HttpServletResponse res) throws Exception {

		SearchForm sf=new SearchForm();
		sf.setSdirection("desc");
		Search ps=Search.getInstance();
		SearchResults sr=ps.performSearch(sf, getUser(req));

		URI myself=new URI(req.getRequestURL().toString());
		String base=new URI(myself.getScheme(), myself.getUserInfo(),
			myself.getHost(), myself.getPort(), req.getContextPath() + "/",
			null, null).toString();

		res.setHeader("Content-type", "text/xml");
		ContentHandler handler=getContentHandler(res);
		new SearchResultsRSSAdaptor(sr, base).writeXml(handler);
		handler.endDocument();
	}

	private static class SearchResultsRSSAdaptor extends RSSChannel {
		private SearchResults results=null;
		private String base=null;
		private SearchResultsRSSAdaptor(SearchResults sr, String b) {
			super("PhotoServlet RSS Feed", b,
				"Experimental RSS feed from Dustin's PhotoServlet");
			base=b;
			results=sr;
		}

		protected Collection<? extends RSSItem> getItems() {
			Collection<PhotoImageDataWrapper> c=
				new ArrayList<PhotoImageDataWrapper>(CHANNEL_SIZE);
			for(PhotoImageData pid : results) {
				// Just get the first CHANNEL_SIZE
				if(c.size() > CHANNEL_SIZE) {
					break;
				}
				c.add(new PhotoImageDataWrapper(pid, base));
			}
			return(c);
		}
	}

	private static class PhotoImageDataWrapper implements RSSItem {
		private PhotoImageData pid=null;
		private String base=null;
		public PhotoImageDataWrapper(PhotoImageData p, String b) {
			super();
			pid=p;
			base=b;
		}

		public String getTitle() {
			String desc=pid.getDescr();
			if(desc.length() > 32) {
				desc=desc.substring(0, 29) + "...";
			}
			return(desc);
		}

		public String getLink() {
			return(base + "display.do?id=" + pid.getId());
		}

		public String getDescription() {
			return("<img src=\""
				+ base + "PhotoServlet?id=" + pid.getId()
					+ "&thumbnail=1"
				+ "\"/><br/>" + pid.getDescr());
		}

		public Date getPubDate() {
			return(pid.getTaken());
		}

		public String getGuid() {
			return(base + "display.do?id=" + pid.getId());
		}
	}

}

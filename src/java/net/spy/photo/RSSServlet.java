// Copyright (c) 2005  Dustin Sallings <dustin@spy.net>

package net.spy.photo;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.spy.jwebkit.rss.RSSChannel;
import net.spy.jwebkit.rss.RSSItem;
import net.spy.photo.ajax.PhotoAjaxServlet;
import net.spy.photo.search.SearchResults;
import net.spy.stat.Stats;

import org.xml.sax.ContentHandler;

/**
 * Servlet for sending RSS data from search results.
 */
public class RSSServlet extends PhotoAjaxServlet {

	private static final int CHANNEL_SIZE=50;

	/** 
	 * Handle the RSS request.
	 */
	@Override
	protected void processRequest(
		HttpServletRequest req, HttpServletResponse res) throws Exception {

		HttpSession ses=req.getSession(false);
		PhotoSessionData sessionData=(PhotoSessionData)ses.getAttribute(
			PhotoSessionData.SES_ATTR);
		SearchResults sr=sessionData.getResults();
		if(sr == null) {
			throw new ServletException("No results.");
		}

		boolean authenticated=sessionData.getUser().isInRole(
			User.AUTHENTICATED);

		URI myself=new URI(req.getRequestURL().toString());
		String base=new URI(myself.getScheme(), myself.getUserInfo(),
			myself.getHost(), myself.getPort(), req.getContextPath() + "/",
			null, null).toString();

		res.setHeader("Content-type", "text/xml");
		ContentHandler handler=getContentHandler(res);
		long start=System.currentTimeMillis();
		new SearchResultsRSSAdaptor(sr, base, authenticated).writeXml(handler);
		handler.endDocument();
		Stats.getComputingStat("rss.search." + sessionData.getUser().getName())
			.add(System.currentTimeMillis() - start);
	}

	static class SearchResultsRSSAdaptor extends RSSChannel {
		private SearchResults results=null;
		private String base=null;
		private boolean authenticated=false;
		SearchResultsRSSAdaptor(SearchResults sr, String b, boolean a) {
			super("PhotoServlet RSS Feed", b,
				"Experimental RSS feed from Dustin's PhotoServlet");
			base=b;
			results=sr;
			authenticated=a;
		}

		@Override
		protected Collection<? extends RSSItem> getItems() {
			Collection<PhotoImageWrapper> c=
				new ArrayList<PhotoImageWrapper>(CHANNEL_SIZE);
			for(PhotoImage pid : results) {
				// Just get the first CHANNEL_SIZE
				if(c.size() >= CHANNEL_SIZE) {
					break;
				}
				c.add(new PhotoImageWrapper(pid, base, authenticated));
			}
			return(c);
		}
	}

	private static class PhotoImageWrapper implements RSSItem {
		private PhotoImage pid=null;
		private String base=null;
		private boolean authenticated=false;
		public PhotoImageWrapper(PhotoImage p, String b, boolean a) {
			super();
			pid=p;
			base=b;
			authenticated=a;
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
			String urlpre=base;
			if(authenticated) {
				urlpre = base + "auth/";
			}
			return("<p><img src=\"" + urlpre + "PhotoServlet?id=" + pid.getId()
				+ "&thumbnail=1"
				+ "\"/><br/>" + pid.getDescr() + "</p>"
				+ "<p>Added " + pid.getTimestamp()
				+ " by " + pid.getAddedBy().getRealname());
		}

		public Date getPubDate() {
			return(pid.getTaken());
		}

		public String getGuid() {
			return(base + "display.do?id=" + pid.getId());
		}
	}

}

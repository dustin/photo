// Copyright (c) 2006  Dustin Sallings <dustin@spy.net>

package net.spy.photo;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.xml.sax.ContentHandler;

import net.spy.jwebkit.rss.RSSChannel;
import net.spy.jwebkit.rss.RSSItem;
import net.spy.photo.ajax.PhotoAjaxServlet;
import net.spy.stat.Stats;

/**
 * RSS feed of recent comments.
 */
public class RSSCommentServlet extends PhotoAjaxServlet {

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

		boolean authenticated=sessionData.getUser().isInRole(
			User.AUTHENTICATED);

		URI myself=new URI(req.getRequestURL().toString());
		String base=new URI(myself.getScheme(), myself.getUserInfo(),
			myself.getHost(), myself.getPort(), req.getContextPath() + "/",
			null, null).toString();

		res.setHeader("Content-type", "text/xml");
		ContentHandler handler=getContentHandler(res);
		long start=System.currentTimeMillis();
		new CommentsRSSAdaptor(
				Comment.getRecentComments(sessionData.getUser(), CHANNEL_SIZE),
				base, authenticated).writeXml(handler);
		handler.endDocument();
		Stats.getComputingStat("rss.comments."
				+ sessionData.getUser().getName())
			.add(System.currentTimeMillis() - start);
	}

	static class CommentsRSSAdaptor extends RSSChannel {
		private List<Comment> comments=null;
		private String base=null;
		private boolean authenticated=false;
		CommentsRSSAdaptor(List<Comment> name,
				String b, boolean a) {
			super("PhotoServlet Comments RSS Feed", b,
				"Experimental Comments RSS feed from Dustin's PhotoServlet");
			base=b;
			comments=name;
			authenticated=a;
		}

		@Override
		protected Collection<? extends RSSItem> getItems() {
			Collection<GroupedCommentsWrapper> c=
				new ArrayList<GroupedCommentsWrapper>(CHANNEL_SIZE);
			for(Comment comment : comments) {
				c.add(new GroupedCommentsWrapper(comment, base, authenticated));
			}
			return(c);
		}
	}

	static class GroupedCommentsWrapper implements RSSItem {
		private Comment comment=null;
		private String base=null;
		private boolean authenticated=false;

		public GroupedCommentsWrapper(Comment c, String b, boolean a) {
			comment=c;
			base=b;
			authenticated=a;
		}

		public String getTitle() {
			return "New comment by " + comment.getUser().getRealname()
				+ " on " + comment.getTimestamp();
		}

		public String getLink() {
			return(base + "display.do?id=" + comment.getPhotoId());
		}

		public String getDescription() {
			String urlpre=base;
			PhotoImageData pid=PhotoImageDataFactory.getInstance().getObject(
					comment.getPhotoId());
			if(authenticated) {
				urlpre = base + "auth/";
			}
			return("<p><img src=\"" + urlpre + "PhotoServlet?id=" + pid.getId()
				+ "&thumbnail=1"
				+ "\"/></p>"
				+ "<p>" + comment.getUser().getRealname()
				+ " commented on " + comment.getTimestamp() + "</p><p>"
				+ comment.getNote() + "</p>");
		}

		public Date getPubDate() {
			return(comment.getTimestamp());
		}

		public String getGuid() {
			return getLink() + "#comment" + comment.getCommentId();
		}
	}
}

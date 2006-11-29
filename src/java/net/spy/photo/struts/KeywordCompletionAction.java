// arch-tag: 24B134D2-7821-42F9-9BAD-8B11DAEFDC84
package net.spy.photo.struts;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.spy.photo.User;
import net.spy.photo.search.KeywordMatch;
import net.spy.photo.search.Search;
import net.spy.photo.search.SearchCache;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * Backend for keyword autocompletion.
 */
@SuppressWarnings("unchecked")
public class KeywordCompletionAction extends PhotoAction {

	private static final int MAX_RESULTS=10;

	private Collection getKeywordCompletions(User user, String prefixMatch)
		throws Exception {
		Collection<KeywordMatch> ts
			=new TreeSet<KeywordMatch>(KeywordMatch.BY_FREQUENCY);
		ts.addAll(Search.getInstance().getKeywordsForUser(user));
		if(prefixMatch != null) {
			for(Iterator<KeywordMatch> i=ts.iterator();
				i.hasNext();) {
				KeywordMatch km=i.next();
				if(!km.getKeyword().getKeyword().startsWith(prefixMatch)) {
					i.remove();
				}
			}
		}
		// Make sure we don't return too many results
		if(ts.size() > MAX_RESULTS) {
			Collection<KeywordMatch> smaller=new ArrayList<KeywordMatch>();
			for(Iterator<KeywordMatch> i=ts.iterator(); i.hasNext()
				&& smaller.size() < MAX_RESULTS; ) {
				smaller.add(i.next());
			}
			ts=smaller;
		}
		return(ts);
	}

	@Override
	protected ActionForward spyExecute(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res) throws Exception {

		User user=getUser(req);
		String prefix="";
		String match=req.getParameter("what");
		if(match == null) {
			match="";
		}
		if(match.length() > 0 && match.startsWith("-")) {
			prefix="-";
			match=match.substring(1);
		}
		SearchCache sc=SearchCache.getInstance();
		CacheKey ck=new CacheKey(user.getId(), match);
		Collection kws=(Collection)sc.get(ck);
		if(kws == null) {
			kws=getKeywordCompletions(user, match);
			sc.store(ck, kws);
		}
		req.setAttribute("keywords", kws);
		req.setAttribute("kwprefix", prefix);
		return(mapping.findForward("next"));
	}

	static class CacheKey {

		private int uid=0;
		private String kw=null;

		public CacheKey(int u, String w) {
			super();
			uid=u;
			kw=w;
		}

		@Override
		public boolean equals(Object o) {
			boolean rv=false;
			if(o instanceof CacheKey) {
				CacheKey k=(CacheKey)o;
				rv = (uid == k.uid && kw.equals(k.kw));
			}
			return(rv);
		}

		@Override
		public int hashCode() {
			return(uid ^ kw.hashCode());
		}
		
	}

}

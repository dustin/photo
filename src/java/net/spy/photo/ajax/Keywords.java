// arch-tag: DE6C47BD-1D7A-4594-B640-B70A14854117
package net.spy.photo.ajax;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeSet;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import net.spy.photo.search.KeywordMatch;
import net.spy.photo.search.Search;
import net.spy.xml.CollectionElement;
import net.spy.xml.SAXAble;

/**
 * Get the keywords for the current user.
 */
public class Keywords extends PhotoAjaxServlet {

	private Map<String, Comparator<KeywordMatch>> paths=null;

	@Override
	public void init(ServletConfig conf) throws ServletException {
		super.init(conf);
		paths=new HashMap<String, Comparator<KeywordMatch>>();
		paths.put("/", KeywordMatch.BY_KEYWORD);
		paths.put("/alph", KeywordMatch.BY_KEYWORD);
		paths.put("/freq", KeywordMatch.BY_FREQUENCY);
	}

	@Override
	@SuppressWarnings("unchecked")
	protected SAXAble getResults(HttpServletRequest request) throws Exception {
		Comparator<KeywordMatch> comp=paths.get(request.getPathInfo());
		if(comp == null) {
			throw new IllegalArgumentException("Illegal path: "
					+ request.getPathInfo() + " try one of " + paths.keySet());
		}
		Collection<KeywordMatch> ts=new TreeSet<KeywordMatch>(comp);
		ts.addAll(Search.getInstance().getKeywordsForUser(getUser(request)));
		String prefixMatch=request.getParameter("keyword");
		if(prefixMatch != null) {
			for(Iterator<KeywordMatch> i=ts.iterator(); i.hasNext();) {
				KeywordMatch km=i.next();
				if(!km.getKeyword().getKeyword().startsWith(prefixMatch)) {
					i.remove();
				}
			}
		}
		return(new CollectionElement("keywords", (Collection)ts));
	}
}
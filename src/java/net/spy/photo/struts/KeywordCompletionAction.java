// arch-tag: 24B134D2-7821-42F9-9BAD-8B11DAEFDC84
package net.spy.photo.struts;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.spy.photo.search.KeywordMatch;
import net.spy.photo.search.Search;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * Backend for keyword autocompletion.
 */
public class KeywordCompletionAction extends PhotoAction {

	private static final int MAX_RESULTS=10;

	protected ActionForward spyExecute(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res) throws Exception {
		Collection ts=new TreeSet(KeywordMatch.BY_FREQUENCY);
		ts.addAll(Search.getInstance().getKeywordsForUser(
			getUser(req)));
		String prefixMatch=req.getParameter("what");
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
			Collection smaller=new ArrayList();
			for(Iterator i=ts.iterator(); i.hasNext()
				&& smaller.size() < MAX_RESULTS; ) {
				smaller.add(i.next());
			}
			ts=smaller;
		}
		req.setAttribute("keywords", ts);
		return(mapping.findForward("next"));
	}

}
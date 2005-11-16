// arch-tag: 6387A516-052C-4A11-BE8E-8232A0F18874
package net.spy.photo.struts;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
 * Compute a cloud for the top 100 keywords.
 */
public class KeywordCloudAction extends PhotoAction {

	private static final int MAX_RESULTS=100;

	private SearchForm getSearchForm(HttpServletRequest req) throws Exception {
		SearchForm sf=new SearchForm();
		if(req.getParameter("recent") != null
			|| req.getParameter("from") != null
			|| req.getParameter("to") != null) {
			String dateFrom=null;
			String dateTo=null;
			if(req.getParameter("recent")!= null) {
				SimpleDateFormat sdf=new SimpleDateFormat("yyyy/MM/dd");
				Calendar cal=Calendar.getInstance();
				cal.add(Calendar.DAY_OF_MONTH, -30);
				dateFrom=sdf.format(cal.getTime());
			} else {
				dateFrom=req.getParameter("from");
				dateTo=req.getParameter("to");
			}
			sf=new SearchForm();
			sf.setStart(dateFrom);
			sf.setEnd(dateTo);
			sf.setOrder("a.ts");
		}
		sf.setSdirection("desc");
		return(sf);
	}

	@Override
	protected ActionForward spyExecute(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res) throws Exception {
		Collection<KeywordMatch> ts=
			new TreeSet<KeywordMatch>(KeywordMatch.BY_FREQUENCY);
		ts.addAll(Search.getInstance().getKeywordsForUser(
			getUser(req), getSearchForm(req)));
		// Make sure we don't return too many results
		if(ts.size() > MAX_RESULTS) {
			Collection smaller=new ArrayList();
			for(Iterator i=ts.iterator(); i.hasNext()
				&& smaller.size() < MAX_RESULTS; ) {
				smaller.add(i.next());
			}
			ts=smaller;
		}

		int sum=0;
		for(KeywordMatch km : ts) {
			sum+= km.getCount();
		}
		Collection<KeywordMatch> buckets[]=new Collection[5];
		for(int i=0; i<buckets.length; i++) {
			buckets[i]=new ArrayList();
		}
		int each=sum/buckets.length;
		int current=0;
		for(KeywordMatch km : ts) {
			if(sumOf(buckets[current]) + km.getCount() > each) {
				current++;
			}
			if(current >= buckets.length) {
				current=buckets.length-1;
			}
			buckets[current].add(km);
		}

		Collection<KeywordMatch> sorted=new TreeSet(KeywordMatch.BY_KEYWORD);
		sorted.addAll(ts);

		Collection rv=new ArrayList(sorted.size());

		for(KeywordMatch km : sorted) {
			for(int i=0; i<buckets.length; i++) {
				if(buckets[i].contains(km)) {
					rv.add(new KW(km, i));
					break;
				}
			}
		}

		req.setAttribute("keywords", rv);
		return(mapping.findForward("next"));
	}

	private int sumOf(Collection<KeywordMatch> collection) {
		int sum=0;
		for(KeywordMatch km : collection) {
			sum+=km.getCount();
		}
		return(sum);
	}

	public static class KW {
		private int bucket=0;
		private KeywordMatch kwmatch=null;
		public KW(KeywordMatch k, int b) {
			super();
			bucket=b;
			kwmatch=k;
		}
		public int getBucket() {
			return bucket;
		}
		public KeywordMatch getKwmatch() {
			return kwmatch;
		}
		
	}
}

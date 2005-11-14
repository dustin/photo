// arch-tag: 6387A516-052C-4A11-BE8E-8232A0F18874
package net.spy.photo.struts;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.spy.photo.KeywordFactory;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * Compute a cloud for the top 100 keywords.
 */
public class KeywordCloudAction extends PhotoAction {

	private static final int MAX_RESULTS=100;

	@Override
	protected ActionForward spyExecute(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res) throws Exception {
		Collection<KeywordFactory.KeywordMatch> ts=
			new TreeSet<KeywordFactory.KeywordMatch>(
					KeywordFactory.KEYWORMATCH_BY_FREQUENCY);
		ts.addAll(KeywordFactory.getInstance().getKeywordsForUser(
			getUser(req)));
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
		for(KeywordFactory.KeywordMatch km : ts) {
			sum+= km.getCount();
		}
		Collection<KeywordFactory.KeywordMatch> buckets[]=new Collection[5];
		for(int i=0; i<buckets.length; i++) {
			buckets[i]=new ArrayList();
		}
		int each=sum/buckets.length;
		int current=0;
		for(KeywordFactory.KeywordMatch km : ts) {
			if(sumOf(buckets[current]) + km.getCount() > each) {
				current++;
			}
			if(current >= buckets.length) {
				current=buckets.length-1;
			}
			buckets[current].add(km);
		}

		Collection<KeywordFactory.KeywordMatch> sorted=
			new TreeSet(KeywordFactory.KEYWORDMATCH_BY_KEYWORD);
		sorted.addAll(ts);

		Collection rv=new ArrayList(sorted.size());

		for(KeywordFactory.KeywordMatch km : sorted) {
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

	private int sumOf(Collection<KeywordFactory.KeywordMatch> collection) {
		int sum=0;
		for(KeywordFactory.KeywordMatch km : collection) {
			sum+=km.getCount();
		}
		return(sum);
	}

	public static class KW {
		private int bucket=0;
		private KeywordFactory.KeywordMatch kwmatch=null;
		public KW(KeywordFactory.KeywordMatch k, int b) {
			super();
			bucket=b;
			kwmatch=k;
		}
		public int getBucket() {
			return bucket;
		}
		public KeywordFactory.KeywordMatch getKwmatch() {
			return kwmatch;
		}
		
	}
}

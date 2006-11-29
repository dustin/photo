// Copyright (c) 2003  Dustin Sallings <dustin@spy.net>
// arch-tag: 29CC6236-5D6E-11D9-9B65-000A957659CC

package net.spy.photo.struts;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.spy.photo.User;
import net.spy.photo.search.ParallelSearch;
import net.spy.photo.search.SavedSearch;
import net.spy.photo.search.SearchResults;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * Load a saved search and overwrite the search form with that search.
 */
public class GetSavedSearchesAction extends PhotoAction {

	/**
	 * Get an instance of GetSavedSearchesAction.
	 */
	public GetSavedSearchesAction() {
		super();
	}

	/**
	 * Load a saved search from a dynaform that has <q>search_id</q>
	 * defined.
	 */
	@Override
	public ActionForward spyExecute(ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,HttpServletResponse response)
		throws Exception {

		User u=getUser(request);

		SortedSet<CardinalSavedSearch> searches=
			new TreeSet<CardinalSavedSearch>();

		Map<SavedSearch, Future<SearchResults>> futureSearches=
			new HashMap<SavedSearch, Future<SearchResults>>();
		ParallelSearch ps=ParallelSearch.getInstance();
		for(SavedSearch ss : SavedSearch.getSearches()) {
			futureSearches.put(ss, ps.futureSearch(ss.getSearchForm(), u));
		}
		for(Map.Entry<SavedSearch, Future<SearchResults>> me
				: futureSearches.entrySet()) {
			int size=me.getValue().get(10, TimeUnit.SECONDS).getSize();
			if(size > 0) {
				searches.add(new CardinalSavedSearch(me.getKey(), size));
			}
		}

		request.setAttribute("searches", searches);

		return(mapping.findForward("next"));
	}

	/**
	 * Saved search with cardinality for the current user.
	 */
	public static class CardinalSavedSearch
		implements Comparable<CardinalSavedSearch>{
		private String name=null;
		private int id=0;
		private int count=0;

		public CardinalSavedSearch(SavedSearch ss, int cnt) {
			name=ss.getName();
			id=ss.getId();
			count=cnt;
		}

		public int getCount() {
			return count;
		}

		public int getId() {
			return id;
		}

		public String getName() {
			return name;
		}

		public int compareTo(CardinalSavedSearch o) {
			return name.compareTo(o.name);
		}
	}

}

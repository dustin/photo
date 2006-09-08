// Copyright (c) 2003  Dustin Sallings <dustin@spy.net>
// arch-tag: 29CC6236-5D6E-11D9-9B65-000A957659CC

package net.spy.photo.struts;

import java.util.ArrayList;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.spy.photo.User;
import net.spy.photo.search.SavedSearch;
import net.spy.photo.search.Search;

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
	public ActionForward spyExecute(ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,HttpServletResponse response)
		throws Exception {

		User u=getUser(request);

		Collection<CardinalSavedSearch> searches=
			new ArrayList<CardinalSavedSearch>();
		Search s=Search.getInstance();
		for(SavedSearch ss : SavedSearch.getSearches()) {
			int size=s.performSearch(ss.getSearchForm(), u).getSize();
			if(size > 0) {
				searches.add(new CardinalSavedSearch(ss, size));
			}
		}

		request.setAttribute("searches", searches);

		return(mapping.findForward("next"));
	}

	/**
	 * Saved search with cardinality for the current user.
	 */
	public static class CardinalSavedSearch {
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
	}

}

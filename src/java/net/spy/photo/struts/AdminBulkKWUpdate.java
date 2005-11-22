// Copyright (c) 2005  Dustin Sallings <dustin@spy.net>
// arch-tag: 79C30BD8-F7FA-4660-97C4-F37D883A54C3

package net.spy.photo.struts;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.spy.db.savables.CollectionSavable;
import net.spy.photo.PhotoImageData;
import net.spy.photo.PhotoImageDataFactory;
import net.spy.photo.Keyword;
import net.spy.photo.KeywordFactory;
import net.spy.photo.impl.SavablePhotoImageData;
import net.spy.photo.search.Search;
import net.spy.photo.search.SearchResults;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.DynaActionForm;

/**
 * Update keywords for images in bulk.
 */
public class AdminBulkKWUpdate extends PhotoAction {

	public ActionForward spyExecute(ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,HttpServletResponse response)
		throws Exception {

		DynaActionForm lf=(DynaActionForm)form;

		SearchForm sf=new SearchForm();
		sf.setWhat((String)lf.get("match"));
		sf.setField("keywords");

		Collection<Keyword> toAdd=new HashSet<Keyword>();
		Collection<Keyword> toRemove=new HashSet<Keyword>();

		KeywordFactory kf=KeywordFactory.getInstance();
		StringTokenizer st = new StringTokenizer((String)lf.get("modify"));
		while(st.hasMoreTokens()) {
			Collection<Keyword> addTo=toAdd;
			String kwstring = st.nextToken();
			if(kwstring.startsWith("-")) {
				kwstring=kwstring.substring(1);
				addTo=toRemove;
			}
			Keyword k = kf.getKeyword(kwstring, true);
			if(k != null) {
				addTo.add(k);
			}
		}

		SearchResults results=Search.getInstance().performSearch(sf,
			getUser(request));

		getLogger().info("Updating " + results.size()
			+ " images by adding " + toAdd + " and removing " + toRemove);

		PhotoImageDataFactory pidf=PhotoImageDataFactory.getInstance();

		ArrayList savables=new ArrayList(results.size());
		for(PhotoImageData pid : results) {
			SavablePhotoImageData savable=new SavablePhotoImageData(
				pidf.getObject(pid.getId()));

			Collection<Keyword> kw=new HashSet<Keyword>(pid.getKeywords());
			kw.addAll(toAdd);
			kw.removeAll(toRemove);

			assert kw.size() > 0 : "Trying to set keywords to the null set.";

			getLogger().info("Setting keywords of " + pid + " from "
				+ pid.getKeywords() + " to " + kw);

			savable.setKeywords(kw);

			savables.add(savable);
		}

		pidf.store(new CollectionSavable(savables));

		request.setAttribute("updated", new Integer(results.size()));

		return(mapping.findForward("next"));
	}

}

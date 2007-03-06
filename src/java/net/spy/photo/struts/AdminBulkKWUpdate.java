// Copyright (c) 2005  Dustin Sallings <dustin@spy.net>

package net.spy.photo.struts;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.DynaActionForm;

import net.spy.db.Savable;
import net.spy.db.savables.CollectionSavable;
import net.spy.photo.Keyword;
import net.spy.photo.KeywordFactory;
import net.spy.photo.PhotoImage;
import net.spy.photo.PhotoImageFactory;
import net.spy.photo.impl.SavablePhotoImage;
import net.spy.photo.search.ParallelSearch;
import net.spy.photo.search.SearchResults;

/**
 * Update keywords for images in bulk.
 */
public class AdminBulkKWUpdate extends PhotoAction {

	@Override
	public ActionForward spyExecute(ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,HttpServletResponse response)
		throws Exception {

		DynaActionForm lf=(DynaActionForm)form;

		SearchForm sf=new SearchForm();
		sf.setWhat((String)lf.get("match"));
		sf.setField("keywords");

		KeywordFactory kf=KeywordFactory.getInstance();
		KeywordFactory.Keywords kws=kf.getKeywords((String)lf.get("modify"), true);

		SearchResults results=ParallelSearch.getInstance().performSearch(sf,
			getUser(request));

		getLogger().info("Updating " + results.getSize()
			+ " images by adding " + kws.getPositive() + " and removing "
			+ kws.getNegative());

		PhotoImageFactory pidf=PhotoImageFactory.getInstance();

		ArrayList<Savable> savables=new ArrayList<Savable>(results.getSize());
		for(PhotoImage pid : results) {
			SavablePhotoImage savable=new SavablePhotoImage(
				pidf.getObject(pid.getId()));

			Collection<Keyword> kw=new HashSet<Keyword>(pid.getKeywords());
			kw.addAll(kws.getPositive());
			kw.removeAll(kws.getNegative());

			assert kw.size() > 0 : "Trying to set keywords to the null set.";

			getLogger().info("Setting keywords of " + pid + " from "
				+ pid.getKeywords() + " to " + kw);

			savable.setKeywords(kw);

			savables.add(savable);
		}

		pidf.store(new CollectionSavable(savables));

		request.setAttribute("updated", new Integer(results.getSize()));

		return(mapping.findForward("next"));
	}

}

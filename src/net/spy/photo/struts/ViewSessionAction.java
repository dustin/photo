// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
// arch-tag: 40F06365-5D6E-11D9-89E9-000A957659CC

package net.spy.photo.struts;

import java.util.Iterator;
import java.io.IOException;

import javax.servlet.ServletException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.DynaActionForm;

import net.spy.util.Base64;

import net.spy.photo.PhotoImageData;
import net.spy.photo.PhotoImageDataFactory;
import net.spy.photo.search.SearchResult;
import net.spy.photo.search.SearchResults;
import net.spy.photo.PhotoSessionData;
import net.spy.photo.SessionWatcher;

/**
 * Turn a session into search results.
 */
public class ViewSessionAction extends PhotoAction {

	/**
	 * Get an instance of ViewSessionAction.
	 */
	public ViewSessionAction() {
		super();
	}

	/**
	 * Perform the action.
	 */
	public ActionForward spyExecute(ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,HttpServletResponse response)
		throws Exception {

		// get my session data
		PhotoSessionData sessionData=getSessionData(request);

		DynaActionForm sf=(DynaActionForm)form;
		String sessIdB64=(String)sf.get("id");

		// The id will be base64 encoded, decode it
		Base64 base64=new Base64();
		String sessId=new String(base64.decode(sessIdB64));
		PhotoSessionData otherData=SessionWatcher.getSessionData(sessId);

		// Grab the images from the session
		SearchResults results=new SearchResults();
		int sid=0;
		PhotoImageDataFactory pidf=PhotoImageDataFactory.getInstance();
		for(Iterator i=otherData.getImageSeenCollection().iterator();
			i.hasNext(); sid++) {

			Integer id=(Integer)i.next();
			PhotoImageData pid=pidf.getData(id.intValue());
			results.add(new SearchResult(pid, sid));
		}

		// Set the viewing size
		results.setMaxSize(sessionData.getOptimalDimensions());
		// Hard code this for now
		results.setMaxRet(5);
		sessionData.setResults(results);
		// Clear out the encoded search
		sessionData.setEncodedSearch(null);

		// next
		return(mapping.findForward("next"));
	}

}

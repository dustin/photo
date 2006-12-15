// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>

package net.spy.photo.struts;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.spy.photo.PhotoImageData;
import net.spy.photo.PhotoImageDataFactory;
import net.spy.photo.PhotoSessionData;
import net.spy.photo.SessionWatcher;
import net.spy.photo.search.SearchResults;
import net.spy.util.Base64;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.DynaActionForm;

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
	@Override
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
		PhotoImageDataFactory pidf=PhotoImageDataFactory.getInstance();
		for(Integer id : otherData.getImageSeenCollection()) {
			PhotoImageData pid=pidf.getObject(id);
			results.add(pid);
		}

		// Set the viewing size
		results.setMaxSize(sessionData.getOptimalDimensions());
		// Hard code this for now
		results.setPageSize(5);
		sessionData.setResults(results);
		// Clear out the encoded search
		sessionData.setEncodedSearch("");

		// next
		return(mapping.findForward("next"));
	}

}

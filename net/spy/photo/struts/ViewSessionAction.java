// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: ViewSessionAction.java,v 1.1 2003/07/25 20:29:35 dustin Exp $

package net.spy.photo.struts;

import java.io.IOException;

import javax.servlet.ServletException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.DynaActionForm;

import net.spy.util.Base64;

import net.spy.photo.PhotoSearchResults;
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

		// Perform the search
		PhotoSearchResults results=new PhotoSearchResults();
		results.addAll(otherData.getImageSeenCollection());
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
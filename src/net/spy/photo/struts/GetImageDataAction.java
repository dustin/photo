// Copyright (c) 2003  Dustin Sallings <dustin@spy.net>
//
// $Id: GetImageDataAction.java,v 1.2 2003/07/23 04:29:26 dustin Exp $

package net.spy.photo.struts;

import java.io.IOException;

import javax.servlet.ServletException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.spy.photo.Persistent;
import net.spy.photo.PhotoSessionData;
import net.spy.photo.PhotoException;
import net.spy.photo.PhotoImageData;
import net.spy.photo.PhotoImageDataFactory;
import net.spy.photo.PhotoSearchResults;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.DynaActionForm;

/**
 * Action to retrieve image data.
 */
public class GetImageDataAction extends PhotoAction {

	private static final String ATTRIBUTE="image";

	/**
	 * Get an instance of GetImageDataAction.
	 */
	public GetImageDataAction() {
		super();
	}

	private PhotoImageData getImageBySearchId(int id, HttpServletRequest req)
		throws Exception {

		PhotoSessionData sessionData=getSessionData(req);
		PhotoSearchResults results=sessionData.getResults();
		if(results == null) {
			throw new PhotoException("No search results");
		}
		PhotoImageData rv = (PhotoImageData)results.get(id);
		return(rv);
	}

	private PhotoImageData getImageById(int id, HttpServletRequest req)
		throws Exception {

		// Check access
		PhotoSessionData sessionData=getSessionData(req);
		Persistent.getSecurity().checkAccess(sessionData.getUser(), id);
		PhotoImageDataFactory pidf=PhotoImageDataFactory.getInstance();
		PhotoImageData rv= pidf.getData(id);
		return(rv);
	}

	/** 
	 * Process the execute.
	 */
	public ActionForward spyExecute(ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,HttpServletResponse response)
		throws Exception {

		// Figure out what's there.
		DynaActionForm f=(DynaActionForm)form;

		Integer searchId=(Integer)f.get("search_id");
		Integer imageId=(Integer)f.get("id");
		if(imageId == null) {
			imageId = (Integer)f.get("image_id");
		}

		// Some lame input validation.
		if(searchId != null && imageId != null) {
			throw new PhotoException(
				"Can't look it up both by search and absolute ID");
		}

		if(searchId == null && imageId == null) {
			throw new PhotoException("Need an ID.");
		}

		// OK, now figure out what kind we want
		PhotoImageData image=null;
		if(searchId != null) {
			image=getImageBySearchId(searchId.intValue(), request);
		} else {
			image=getImageById(imageId.intValue(), request);
		}
		request.setAttribute(ATTRIBUTE, image);

		ActionForward rv=mapping.findForward("next");
		return(rv);
	}

}

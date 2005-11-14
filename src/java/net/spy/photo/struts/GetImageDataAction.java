// Copyright (c) 2003  Dustin Sallings <dustin@spy.net>
// arch-tag: 26A1BB08-5D6E-11D9-A1B9-000A957659CC

package net.spy.photo.struts;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.spy.photo.Persistent;
import net.spy.photo.PhotoException;
import net.spy.photo.PhotoImageData;
import net.spy.photo.PhotoImageDataFactory;
import net.spy.photo.PhotoSessionData;
import net.spy.photo.PhotoUtil;
import net.spy.photo.search.SearchResults;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.DynaActionForm;

/**
 * Action to retrieve image data.
 */
public class GetImageDataAction extends PhotoAction {

	private static final String ATTRIBUTE="image";
	private static final String DIM_ATTR="displayDims";

	/**
	 * Get an instance of GetImageDataAction.
	 */
	public GetImageDataAction() {
		super();
	}

	private PhotoImageData getImageBySearchId(int id, HttpServletRequest req)
		throws Exception {

		PhotoSessionData sessionData=getSessionData(req);
		SearchResults results=sessionData.getResults();
		if(results == null) {
			throw new PhotoException("No search results");
		}
		PhotoImageData rv = results.get(id);
		return(rv);
	}

	private PhotoImageData getImageById(int id, HttpServletRequest req)
		throws Exception {

		// Check access
		PhotoSessionData sessionData=getSessionData(req);
		Persistent.getSecurity().checkAccess(sessionData.getUser(), id);
		PhotoImageDataFactory pidf=PhotoImageDataFactory.getInstance();
		PhotoImageData rv= pidf.getObject(id);
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

		PhotoSessionData sessionData=getSessionData(request);
		// We will need the display dimensions for some tasks
		request.setAttribute(DIM_ATTR,
			PhotoUtil.scaleTo(image.getDimensions(),
			sessionData.getOptimalDimensions()));

		ActionForward rv=mapping.findForward("next");
		return(rv);
	}

}

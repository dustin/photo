// Copyright (c) 2006  Dustin Sallings <dustin@spy.net>

package net.spy.photo.struts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import net.spy.photo.Place;
import net.spy.photo.PlaceFactory;

/**
 * List all of the places into <code>places</code>.
 */
public class ListPlacesAction extends PhotoAction {

	@Override
	protected ActionForward spyExecute(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res) throws Exception {

		List<Place> places=new ArrayList<Place>(
				PlaceFactory.getInstance().getObjects());
		Collections.sort(places, new Comparator<Place>(){
			public int compare(Place p1, Place p2) {
				return p1.getName().compareTo(p2.getName());
			}});
		req.setAttribute("places", places);

		return mapping.findForward("next");
	}

}

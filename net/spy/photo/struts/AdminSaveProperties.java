// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: AdminSaveProperties.java,v 1.4 2003/07/23 04:29:26 dustin Exp $

package net.spy.photo.struts;

import java.io.IOException;

import java.util.Map;
import java.util.Iterator;

import javax.servlet.ServletException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import net.spy.db.Saver;
import net.spy.db.SaveException;

import net.spy.cache.SpyCache;

import net.spy.photo.PhotoConfig;
import net.spy.photo.PhotoProperties;

/**
 * Save the properties.
 */
public class AdminSaveProperties extends AdminAction {

	/**
	 * Get an instance of AdminSaveProperties.
	 */
	public AdminSaveProperties() {
		super();
	}

	/**
	 * Perform the action.
	 */
	public ActionForward spyExecute(ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,HttpServletResponse response)
		throws Exception {

		PhotoProperties props=null;
		props=new PhotoProperties();
		props.clear();

		for(Iterator i=request.getParameterMap().entrySet().iterator();
			i.hasNext();) {

			Map.Entry me=(Map.Entry)i.next();
			String key=(String)me.getKey();
			String vals[]=(String[])me.getValue();

			if(vals.length>1) {
				throw new ServletException("Too many values for " + key);
			}

			// Special case for ignoring the submit button
			if(!key.equals("submit")) {
				props.setProperty(key, vals[0]);
			}
		}

		Saver s=new Saver(new PhotoConfig());
		s.save(props);
		System.err.println("Saved new properties:  " + props);

		// Clear the cache to make the values immediately available
		SpyCache sc=SpyCache.getInstance();
		sc.uncache("photo_props");

		return(mapping.findForward("next"));
	}

}


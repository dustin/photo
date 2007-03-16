// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>

package net.spy.photo.struts;

import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import net.spy.cache.SimpleCache;
import net.spy.db.Saver;
import net.spy.photo.PhotoConfig;
import net.spy.photo.PhotoProperties;

/**
 * Save the properties.
 */
@SuppressWarnings("unchecked")
public class AdminSaveProperties extends PhotoAction {

	/**
	 * Perform the action.
	 */
	@Override
	public ActionForward spyExecute(ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,HttpServletResponse response)
		throws Exception {

		PhotoProperties props=null;
		props=PhotoProperties.getInstance();
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

		Saver s=new Saver(PhotoConfig.getInstance());
		s.save(props);
		System.err.println("Saved new properties:  " + props);

		// Clear the cache to make the values immediately available
		SimpleCache sc=SimpleCache.getInstance();
		sc.remove("photo_props");

		addMessage(request, MessageType.success, "Saved properties.");

		return(mapping.findForward("next"));
	}

}


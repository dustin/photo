// Copyright (c) 2003  Dustin Sallings <dustin@spy.net>
//
// $Id: AdminReportAction.java,v 1.7 2003/08/09 03:46:50 dustin Exp $

package net.spy.photo.struts;

import java.io.IOException;
import java.lang.reflect.Constructor;

import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.Enumeration;

import javax.servlet.ServletException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import net.spy.SpyConfig;
import net.spy.db.DBSP;
import net.spy.db.CachedResultSet;

import net.spy.jwebkit.struts.DBSPAction;

import net.spy.photo.PhotoConfig;

/**
 * Report fetch action.
 */
public class AdminReportAction extends DBSPAction {

	/**
	 * Get an instance of AdminReportAction.
	 */
	public AdminReportAction() {
		super();
	}

	/** 
	 * Get the PhotoConfig.
	 */
	protected SpyConfig getSpyConfig() {
		return(new PhotoConfig());
	}

	/** 
	 * Perform this action.
	 */
	/*
	public ActionForward spyExecute(ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,HttpServletResponse response)
		throws Exception {

		// Verify an admin user
		checkAdmin(request);

		// Cast the mapping
		AdminReportMapping arm=(AdminReportMapping)mapping;

		// Get the report name
		request.setAttribute("reportName", arm.getReportName());

		// Get the spt
		DBSP db=constructDBSP(arm.getSptClass());

		// Format p.s. for string.  p.i. for integer, p.f. for float, etc...
		for(Enumeration e=request.getParameterNames();
			e.hasMoreElements();) {

			String s=(String)e.nextElement();
			String pn=s.substring(4);
			String v=request.getParameter(s);
			if(s.startsWith("p.s.")) {
				db.set(pn, v);
			} else if(s.startsWith("p.i.")) {
				db.set(pn, Integer.parseInt(v));
			} else if(s.startsWith("p.f.")) {
				db.set(pn, Float.parseFloat(v));
			}
		}

		ResultSet rs=db.executeQuery();

		// Store the result set
		request.setAttribute("rs", new CachedResultSet(rs));

		db.close();
		// It's closed, but it still has useful information
		request.setAttribute("db", db);
		// Location of this URL
		request.setAttribute("rurl", request.getRequestURI());

		return(mapping.findForward("next"));
	}
*/

}

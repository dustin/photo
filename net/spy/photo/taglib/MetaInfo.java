// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: MetaInfo.java,v 1.5 2003/08/09 07:32:58 dustin Exp $

package net.spy.photo.taglib;

import java.sql.ResultSet;

import java.text.NumberFormat;

import javax.servlet.jsp.JspException;

import net.spy.SpyDB;

import net.spy.cache.SpyCache;

import net.spy.photo.PhotoConfig;

/**
 * Meta information about the site.
 */
public class MetaInfo extends PhotoTag {

	/**
	 * Get an instance of MetaInfo.
	 */
	public MetaInfo() {
		super();
	}

	private int getCount(String query) throws Exception {
		String key="photo.metainfo." + query;

		SpyCache cache=SpyCache.getInstance();

		int rv=-1;

		Integer rvi=(Integer)cache.get(key);
		if(rvi==null) {
			SpyDB db=new SpyDB(new PhotoConfig());
			ResultSet rs=db.executeQuery(query);
			rs.next();
			rv=rs.getInt(1);
			// Store it for an hour.
			cache.store(key, new Integer(rv), 3600*1000);
			db.close();
		} else {
			rv=rvi.intValue();
		}

		return(rv);
	}

	/**
	 * Get the meta info and shove it into the webpage.
	 */
	public int doStartTag() throws JspException {
		int totalShown=0;
		int totalImages=0;

		try {
			totalShown=getCount(
				"select count(*) from photo_logs\n"
					+ " where log_type=get_log_type('ImgView')");
			totalImages=getCount("select count(*) from album");
		} catch(Exception e) {
			e.printStackTrace();
			throw new JspException("Problem loading meta info " + e);
		}

		pageContext.setAttribute("metaShown", new Integer(totalShown));
		pageContext.setAttribute("metaImages", new Integer(totalImages));

		// Also add these to the request scope
		pageContext.getRequest().setAttribute("metaShown",
			new Integer(totalShown));
		pageContext.getRequest().setAttribute("metaImages",
			new Integer(totalImages));

		return(EVAL_BODY_INCLUDE);
	}

}

// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
// arch-tag: 500CAD7B-5D6E-11D9-BC13-000A957659CC

package net.spy.photo.taglib;

import java.sql.ResultSet;

import javax.servlet.jsp.JspException;

import net.spy.cache.SimpleCache;
import net.spy.db.SpyDB;
import net.spy.photo.PhotoConfig;
import net.spy.photo.PhotoImageDataFactory;

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

		SimpleCache cache=SimpleCache.getInstance();

		int rv=-1;

		Integer rvi=(Integer)cache.get(key);
		if(rvi==null) {
			SpyDB db=new SpyDB(PhotoConfig.getInstance());
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
	@Override
	public int doStartTag() throws JspException {
		int totalImages=
			PhotoImageDataFactory.getInstance().getObjects().size();
		int totalShown=0;

		try {
			totalShown=getCount(
				"select count(*) from photo_logs\n"
					+ " where log_type=get_log_type('ImgView')");
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

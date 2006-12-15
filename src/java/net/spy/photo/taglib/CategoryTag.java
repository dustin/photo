// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: CategoryTag.java,v 1.7 2003/08/01 04:02:21 dustin Exp $

package net.spy.photo.taglib;

import java.util.Collection;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import net.spy.photo.CategoryFactory;
import net.spy.photo.PhotoException;
import net.spy.photo.PhotoSessionData;
import net.spy.util.SpyUtil;

/**
 * Present category lists.
 */
public class CategoryTag extends PhotoTag {

	private boolean showViewable=false;
	private boolean showAddable=false;

	/**
	 * Get an instance of CategoryTag.
	 */
	public CategoryTag() {
		super();
	}

	public void setShowViewable(boolean to) {
		this.showViewable=to;
	}

	public void setShowViewable(String to) {
		Boolean b=SpyUtil.getBoolean(to);
		this.showViewable=b.booleanValue();
	}

	public boolean getShowViewable() {
		return(showViewable);
	}

	public void setShowAddable(boolean to) {
		this.showAddable=to;
	}

	public void setShowAddable(String to) {
		Boolean b=SpyUtil.getBoolean(to);
		this.showAddable=b.booleanValue();
	}

	public boolean getShowAddable() {
		return(showAddable);
	}

	/**
	 * Provide the variable.
	 */
	@Override
	public int doStartTag() throws JspException {
		int access=0;
		if(showViewable) {
			access|=CategoryFactory.ACCESS_READ;
		}
		if(showAddable) {
			access|=CategoryFactory.ACCESS_WRITE;
		}

		// Get the session data
		PhotoSessionData sessionData=(PhotoSessionData)
			pageContext.getAttribute(PhotoSessionData.SES_ATTR,
				PageContext.SESSION_SCOPE);

		Collection<?> cats=null;
		try {
			CategoryFactory cf=CategoryFactory.getInstance();
			cats=cf.getCatList(sessionData.getUser().getId(), access);
		} catch(PhotoException pe) {
			pe.printStackTrace();
			throw new JspException("Error getting category list.");
		} catch(RuntimeException e) {
			e.printStackTrace();
			throw e;
		}

		pageContext.setAttribute("catList", cats);

		return(EVAL_BODY_INCLUDE);
	}

	@Override
	public int doEndTag() throws JspException {
		pageContext.removeAttribute("catList");

		return(EVAL_PAGE);
	}

}

// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: CategoryTag.java,v 1.1 2002/05/11 09:24:34 dustin Exp $

package net.spy.photo.taglib;

import java.util.*;

import javax.servlet.*;
import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;

import net.spy.photo.*;

/**
 * Present category lists.
 */
public class CategoryTag extends TagSupport {

	private boolean showViewable=false;
	private boolean showAddable=false;

	/**
	 * Get an instance of CategoryTag.
	 */
	public CategoryTag() {
		super();
	}

	public void setShowViewable(boolean showViewable) {
		this.showViewable=showViewable;
	}

	public void setShowViewable(String showViewable) {
		Boolean b=new Boolean(showViewable);
		this.showViewable=b.booleanValue();
	}

	public boolean getShowViewable() {
		return(showViewable);
	}

	public void setShowAddable(boolean showAddable) {
		this.showAddable=showAddable;
	}

	public void setShowAddable(String showAddable) {
		Boolean b=new Boolean(showAddable);
		this.showAddable=b.booleanValue();
	}

	public boolean getShowAddable() {
		return(showAddable);
	}

	/**
	 * Provide the variable.
	 */
	public int doStartTag() throws JspException {
		int access=0;
		if(showViewable) {
			access|=Category.ACCESS_READ;
		}
		if(showAddable) {
			access|=Category.ACCESS_WRITE;
		}

		// Get the session data
		PhotoSessionData sessionData=(PhotoSessionData)
			pageContext.getAttribute("sessionData", PageContext.REQUEST_SCOPE);

		Collection cats=null;
		try {
			cats=Category.getCatList(sessionData.getUser().getId(), access);
		} catch(PhotoException pe) {
			pe.printStackTrace();
			throw new JspException("Error getting category list.");
		}

		pageContext.setAttribute("catList", cats);

		return(EVAL_BODY_INCLUDE);
	}

	public int doEndTag() throws JspException {
		pageContext.removeAttribute("catList");

		return(EVAL_PAGE);
	}

}

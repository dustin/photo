// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: CategoryTagExtraInfo.java,v 1.1 2002/05/11 09:24:34 dustin Exp $

package net.spy.photo.taglib;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;

/**
 * Variables that get set to hold a category list.
 */
public class CategoryTagExtraInfo extends TagExtraInfo {

	/**
	 * Get the variable info
	 */
	public VariableInfo[] getVariableInfo(TagData data) {
		return(new VariableInfo[] {
			new VariableInfo(
				"catList", "java.util.Collection", true, VariableInfo.NESTED)});
	}

}

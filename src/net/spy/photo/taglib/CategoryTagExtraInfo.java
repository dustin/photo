// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
// arch-tag: 49A03DCE-5D6E-11D9-ACDB-000A957659CC

package net.spy.photo.taglib;

import javax.servlet.jsp.tagext.TagData;
import javax.servlet.jsp.tagext.TagExtraInfo;
import javax.servlet.jsp.tagext.VariableInfo;

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

// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: ImageDataTagExtraInfo.java,v 1.1 2002/05/15 08:26:15 dustin Exp $

package net.spy.photo.taglib;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;

/**
 * Describes the data stored by the image tag.
 */
public class ImageDataTagExtraInfo extends TagExtraInfo {

	/**
	 * Get the variable info.
	 */
	public VariableInfo[] getVariableInfo(TagData data) {
		return(new VariableInfo[] {
			new VariableInfo(
				"image", "net.spy.photo.PhotoSearchResult", true,
				VariableInfo.NESTED)});
	}

}

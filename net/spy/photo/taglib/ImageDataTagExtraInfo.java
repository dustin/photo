// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: ImageDataTagExtraInfo.java,v 1.2 2002/05/21 07:45:09 dustin Exp $

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
				"image", "net.spy.photo.PhotoImageData", true,
				VariableInfo.NESTED)});
	}

}

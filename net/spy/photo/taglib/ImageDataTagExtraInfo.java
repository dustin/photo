// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: ImageDataTagExtraInfo.java,v 1.3 2002/07/10 03:38:09 dustin Exp $

package net.spy.photo.taglib;

import javax.servlet.jsp.tagext.TagData;
import javax.servlet.jsp.tagext.TagExtraInfo;
import javax.servlet.jsp.tagext.VariableInfo;

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

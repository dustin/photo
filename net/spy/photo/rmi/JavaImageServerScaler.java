// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: JavaImageServerScaler.java,v 1.1 2002/06/16 07:14:13 dustin Exp $

package net.spy.photo.rmi;

import net.spy.photo.*;

/**
 * Scale the image in 100% Java.
 */
public class JavaImageServerScaler extends ImageServerScaler {

	/**
	 * Get an instance of JavaImageServerScaler.
	 */
	public JavaImageServerScaler() {
		super();
	}

	/**
	 * Scale it in 100% Java.
	 */
	public PhotoImage scaleImage(PhotoImage in, PhotoDimensions dim)
		throws Exception {

		if(in.getFormat()!=PhotoImage.FORMAT_JPEG) {
			throw new Exception("JavaImageServerScaler does not yet handle "
				+ in.getFormatString() + " images.");
		}

		PhotoImageScaler pis=new PhotoImageScaler(in);
		return(pis.getScaledImage(dim, 70));
	}

}

// Copyright (c) 2001  Dustin Sallings <dustin@spy.net>
//
// $Id: JavaImageServerScaler.java,v 1.1 2002/02/21 07:25:58 dustin Exp $

package net.spy.rmi;

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

		PhotoImageScaler pis=new PhotoImageScaler(in);
		return(pis.getScaledImage(dim, 70));
	}

}
